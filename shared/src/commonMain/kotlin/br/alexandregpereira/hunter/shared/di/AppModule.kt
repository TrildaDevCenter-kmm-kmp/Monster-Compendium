/*
 * Copyright 2023 Alexandre Gomes Pereira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.alexandregpereira.hunter.shared.di

import br.alexandregpereira.hunter.data.di.dataModules
import br.alexandregpereira.hunter.domain.di.domainModules
import br.alexandregpereira.hunter.event.monster.detail.MonsterDetailEvent
import br.alexandregpereira.hunter.event.monster.detail.MonsterDetailEventDispatcher
import br.alexandregpereira.hunter.event.monster.detail.MonsterDetailEventListener
import br.alexandregpereira.hunter.folder.preview.event.FolderPreviewEvent
import br.alexandregpereira.hunter.folder.preview.event.FolderPreviewEventDispatcher
import br.alexandregpereira.hunter.folder.preview.event.FolderPreviewResult
import br.alexandregpereira.hunter.folder.preview.event.FolderPreviewResultListener
import br.alexandregpereira.hunter.monster.compendium.state.di.monsterCompendiumStateModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModules(): List<Module> = domainModules + dataModules +
        monsterCompendiumStateModule +
        module {
            factory { Dispatchers.Default }
            // TODO Remove later
            factory<MonsterDetailEventListener> {
                object : MonsterDetailEventListener {
                    override val events: Flow<MonsterDetailEvent>
                        get() = emptyFlow()
                }
            }
            factory<MonsterDetailEventDispatcher> {
                object : MonsterDetailEventDispatcher {
                    override fun dispatchEvent(event: MonsterDetailEvent) {}
                }
            }
            factory<FolderPreviewResultListener> {
                object : FolderPreviewResultListener {
                    override val result: Flow<FolderPreviewResult>
                        get() = emptyFlow()
                }
            }
            factory<FolderPreviewEventDispatcher> {
                object : FolderPreviewEventDispatcher {
                    override fun dispatchEvent(event: FolderPreviewEvent) {}
                }
            }
        }