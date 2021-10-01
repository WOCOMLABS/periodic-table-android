package com.labs.wocom.android.compose

import co.touchlab.kermit.Kermit
import com.labs.wocom.feature_periodic_table.AKARendererPeriodicTable
import com.labs.wocom.feature_periodic_table.domain.StatePeriodicTable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 * Renderer periodic table
 *
 * @constructor Create empty Renderer periodic table
 */
class RendererPeriodicTable : AKARendererPeriodicTable, KoinComponent {


    /**
     * Logger
     */
    val logger : Kermit by inject()
    /**
     * Json
     */
    val json : Json by inject()


    /**
     * Render
     *
     * @param state
     * @return
     */
    override fun render(index : Int, state : StatePeriodicTable) = when (state) {

        is StatePeriodicTable.PeriodicTableData -> {
            // logger.d { " ${this@RendererPeriodicTable.hashCode()} with Success ${state.uuid} ${Thread.currentThread().id} ${Thread.activeCount()} " }
            /* logger.d {
                     json.encodeToString(state.data.elements.map { e -> e.name })
                 }*/
        }

        is StatePeriodicTable.ChemicalElementData -> {
            //logger.d { " ${this@RendererPeriodicTable.hashCode()}  with ChemmicalElement ${state.uuid} ${Thread.currentThread().id} ${Thread.activeCount()} " }
            /*  logger.d {
                      json.encodeToString(state.data.map { it.number to it.name })
                  }*/
        }

        is StatePeriodicTable.InitialData -> {
            // logger.d { " ${this@RendererPeriodicTable.hashCode()}  with initial ${state.uuid} ${Thread.currentThread().id} ${Thread.activeCount()} " }
        }

        is StatePeriodicTable.LoadingData -> {
            if (state.isSideEffect) {
                logger.d { " ${this@RendererPeriodicTable.hashCode()}  with loading Side effect ${state.correlationId} ${Thread.currentThread().id} ${Thread.activeCount()} " }
            } else {
                logger.d { " ${this@RendererPeriodicTable.hashCode()}  with loading ${state.correlationId} ${Thread.currentThread().id} ${Thread.activeCount()} " }
            }
        }

        is StatePeriodicTable.SideEffect.CatastrophicError -> {
            //logger.d { " with sideEffect catastrophic error ${state.uuid} ${Thread.currentThread().id} ${Thread.activeCount()} " }
        }

        is StatePeriodicTable.SideEffect.Message -> {
            // logger.d { " with side effect message ${state.uuid} ${Thread.currentThread().id} ${Thread.activeCount()} " }
        }
    }


}
