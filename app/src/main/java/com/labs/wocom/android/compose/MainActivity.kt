package com.labs.wocom.android.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.labs.wocom.android.compose.ui.theme.ComposeTheme
import com.labs.wocom.feature_periodic_table.AKAFeaturePeriodicTable
import com.labs.wocom.feature_periodic_table.AKAIntentServiceInterceptorPeriodicTable
import com.labs.wocom.feature_periodic_table.domain.IntentPeriodicTable
import com.labs.wocom.feature_periodic_table.domain.StatePeriodicTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


@InternalCoroutinesApi
class MainActivity : ComponentActivity() {

    private val feature: AKAFeaturePeriodicTable by inject()
    private val intentInterceptor: AKAIntentServiceInterceptorPeriodicTable by inject()
    private val mutableStateFlowQuery by inject<MutableStateFlow<String>>()

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {

            launch(Dispatchers.Default) {
                mutableStateFlowQuery
                    .asStateFlow()
                    .buffer()
                    .onEach { query ->
                        feature.pulse(
                            intentInterceptor use if (query.isEmpty()) {
                                IntentPeriodicTable.Start
                            } else {
                                IntentPeriodicTable.SearchElement(query)
                            }
                        )
                    }
                    .collect()
            }
        }

        setContent {
            ComposeTheme {
                Scaffold(
                    topBar = {
                        Toolbar(feature.state())
                    },
                    content = {
                        Dashboard(
                            stateFlow = feature.state(),
                            query = mutableStateFlowQuery
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTheme {
        Greeting("Android")
    }
}

@Composable
fun Toolbar(stateFlow: StateFlow<StatePeriodicTable>) {
    val state = stateFlow.collectAsState()
    TopAppBar(
        title = {
            Text(
                text = state.value.correlationId.toString().split("-").let { it[it.size - 1] }
            )
        },
    )
}

@ExperimentalMaterialApi
@Composable
fun Dashboard(
    stateFlow: StateFlow<StatePeriodicTable>,
    modifier: Modifier = Modifier,
    query: MutableStateFlow<String>
) {

    Column(
        modifier = modifier
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        with(stateFlow.collectAsState().value) {

            OutlinedTextField(
                value = query.collectAsState().value,
                onValueChange = {
                    query.value = it
                },
                label = { Text("query") }
            )


            when (this) {
                is StatePeriodicTable.ChemicalElementData -> {
                    data.sortedBy { a -> a.number }.map { chemicalElement ->
                        Card(
                            elevation = 1.dp,
                            shape = RoundedCornerShape(20.dp),
                        ) {
                            ListItem(
                                modifier = Modifier.clickable {},
                                text = { Text(text = chemicalElement.symbol) },
                                secondaryText = { Text(text = chemicalElement.name) },
                                trailing = {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = chemicalElement.summary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                is StatePeriodicTable.InitialData -> {
                }
                is StatePeriodicTable.LoadingData -> {
                }
                is StatePeriodicTable.PeriodicTableData -> {
                }
                is StatePeriodicTable.SideEffect.CatastrophicError -> {
                }
                is StatePeriodicTable.SideEffect.Message -> {
                }
            }
        }

    }

}
