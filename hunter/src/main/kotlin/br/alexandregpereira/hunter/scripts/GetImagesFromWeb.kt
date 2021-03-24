/*
 * Copyright (c) 2021 Alexandre Gomes Pereira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.alexandregpereira.hunter.scripts

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.jsoup.Jsoup

private const val TOTAL_PAGES = 26
private const val URL =
    "https://www.dndbeyond.com/monsters?filter-search=&filter-source=5&filter-source-category=1&filter-type=0&page="
private const val FIRST_URL =
    "https://www.dndbeyond.com/monsters?filter-type=0&filter-search=&filter-cr-min=&filter-cr-max=&filter-armor-class-min=&filter-armor-class-max=&filter-average-hp-min=&filter-average-hp-max=&filter-is-legendary=&filter-is-mythic=&filter-has-lair=&filter-source-category=1&filter-source=5"

@FlowPreview
@ExperimentalCoroutinesApi
suspend fun main() {
//    flowOf(9)
    (20..TOTAL_PAGES).asSequence().asFlow()
        .flatMapConcat { page ->
            getImages(page)
        }.map { pairs ->
            pairs.map {
                WebImageData(
                    name = it.first,
                    url = it.second
                )
            }
        }
        .reduce { acc, value -> acc + value }
        .onEach {
            println(it)
        }
        .let { images ->
            val oldImages = json.decodeFromString<HashSet<WebImageData>>(readJsonFile(JSON_IMAGES_FILE_NAME))
            println(oldImages)
            oldImages.addAll(images.toHashSet())
            saveJsonFile(oldImages.sortedBy { it.name }, JSON_IMAGES_FILE_NAME)
        }
}

@Suppress("BlockingMethodInNonBlockingContext")
private fun getImages(page: Int): Flow<List<Pair<String, String>>> {
    val url = if (page == 1) FIRST_URL else URL + page
    return flow {
        delay(5000)
        val referer = if (page == 1) "https://www.dndbeyond.com/monsters" else URL + (page - 1)
        println("Getting: $url")
        println("Referer: $referer")
        val doc = Jsoup.connect(url)
            .headers(getHeaders(referer))
            .get()
        emit(doc)
    }.map { doc ->
        val elements = doc.select("a[href*=/avatars/thumbnails/]")
        println("Elements founded: ${elements.size}")
        elements.map {
            it.parent().parent().attr("data-slug") to it.attr("href")
        }
    }.catch {
        println(it.toString())
        emptyList<String>()
    }
}

private fun test(page: Int): Flow<List<Pair<String, String>>> {
    return flow {
        println("Getting: $page")
        emit(page)
    }.map { url2 ->
        val elements = listOf(url2)
        println("Elements founded: ${elements.size}")
        elements.map {
            "a" to it.toString()
        }
    }.catch {
        println(it.message)
        emptyList<String>()
    }
}

private fun getHeaders(referer: String): Map<String, String> {
    return mapOf(
        "accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
        "accept-encoding" to "gzip, deflate, br",
        "accept-language" to "en-US,en;q=0.9,pt-BR;q=0.8,pt;q=0.7",
        "referer" to referer,
        "user-agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36",
    )
}


@Serializable
data class WebImageData(
    val name: String,
    val url: String
)