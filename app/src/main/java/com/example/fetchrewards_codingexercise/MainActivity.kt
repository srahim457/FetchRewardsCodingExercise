package com.example.fetchrewards_codingexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fetchrewards_codingexercise.ui.theme.FetchRewards_CodingExerciseTheme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FetchRewards_CodingExerciseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ItemListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ItemListScreen(modifier: Modifier = Modifier) {
    var items by remember { mutableStateOf<List<Entry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // listId sections are expandable dropdown lists with items, this tracks the sections
    // All start expanded for readability of the entire list
    val expandedSections = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(Unit) {
        try {
            val response = ApiService.instance.getItems()
            if (response.isSuccessful){
                //removes the blank names
                val filteredItems = response.body() ?.filter { !it.name.isNullOrBlank() } ?: emptyList()
                items = filteredItems
                //Initialize all sections as expanded
                val listIds = filteredItems.map { it.listId }.distinct()
                listIds.forEach { listId ->
                    expandedSections[listId] = true
                }
            } else {
                errorMessage = "Failed to load"
            }
        } catch (e: Exception) {
            errorMessage = e.message
        }
        isLoading = false
    }

    //when testing the app, the lists were long so I wanted a feature to collapse all the lists
    //clicking the logo collapses/expands all the lists
    val toggleAllSections = {
        val allExpanded = expandedSections.values.all { it }
        expandedSections.keys.forEach { listId ->
            expandedSections[listId] = !allExpanded
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        //fix the header on top to access the logo button
        AppHeader(onLogoClick = toggleAllSections)
        HorizontalDivider()

        when {
            // Display a loading indicator
            isLoading -> {
                Box( modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box( modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text("$errorMessage")
                }
            }

            else -> {
                //sorting the items
                val groupedItems = items
                    .groupBy { it.listId }
                    .toSortedMap()
                    .mapValues { (_, groupItems) -> groupItems.sortedBy {it.id} }
                    // all the names are "Item {id}" so I just sorted by id and displayed the name

                LazyColumn {
                    for ((listId, groupItems) in groupedItems) {
                        item {
                            ClickableSectionHeader(
                                listId = listId,
                                itemCount = groupItems.size,
                                isExpanded = expandedSections[listId] ?: false,
                                onToggle = {
                                    expandedSections[listId] = !(expandedSections[listId] ?: false)
                                }
                            )
                        }
                        if (expandedSections[listId] == true) {
                            items(groupItems) { item ->
                                ItemRow(item = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

//Lists out the Names per line
@Composable
fun ItemRow(item: Entry) {
    Text(
        text = "${item.name}",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 4.dp)
    )
}

//the listId section headers are dropdown collapsible lists
@Composable
fun ClickableSectionHeader( listId: Int, itemCount: Int, isExpanded: Boolean, onToggle: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "List ID: $listId ($itemCount items)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (isExpanded) "▼" else "▶",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

//The top of the page has the title and the logo
@Composable
fun AppHeader(onLogoClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondary,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically){
                Image(
                    painter = painterResource(id = R.drawable.fetch_logo),
                    contentDescription = "Fetch Logo - Click to expand/collapse all",
                    modifier = Modifier
                        .size(80.dp)
                        .clickable { onLogoClick() }
                        .padding(end = 10.dp)
                )
                Column { Text(
                                text = "Fetch Rewards\nCoding Exercise",
                                style = MaterialTheme.typography.titleLarge
                                )
                }
            }
        }
    }
}