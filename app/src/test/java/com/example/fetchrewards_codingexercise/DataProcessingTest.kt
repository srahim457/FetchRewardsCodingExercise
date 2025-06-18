package com.example.fetchrewards_codingexercise

import junit.framework.TestCase.assertEquals
import org.junit.Test

class DataProcessingTest {
    @Test
    fun filterNullOrEmptyNames(){
        val data = listOf(
            Entry(id = 1, listId = 1, name = "Item 1"),
            Entry(id = 2, listId = 1, name = ""),
            Entry(id = 3, listId = 1, name = "   "),
            Entry(id = 4, listId = 2, name = "Item 4"),
            Entry(id = 5, listId = 2, name = "")
        )

        //My filter method
        val filteredList = data.filter { !it.name.isNullOrBlank() }

        assertEquals(2, filteredList.size)
        assertEquals("Item 1", filteredList[0].name)
        assertEquals("Item 4", filteredList[1].name)
    }

    @Test
    fun itemSorting(){

        val data = listOf(
            Entry(id = 2, listId = 1, name = "Item 2"),
            Entry(id = 1, listId = 1, name = "Item 1"),
            Entry(id = 4, listId = 2, name = "Item 4"),
            Entry(id = 5, listId = 2, name = "Item 5")
        )

        //My sorting method
        val groupedItems = data
            .groupBy { it.listId }
            .toSortedMap()
            .mapValues { (_, groupItems) -> groupItems.sortedBy {it.id} }

        assertEquals(2, groupedItems.size)
        val listId1 = groupedItems[1]!!
        assertEquals(2, listId1.size)
        assertEquals("Item 1", listId1[0].name)
        assertEquals("Item 2", listId1[1].name)
        val listId2 = groupedItems[2]!!
        assertEquals(2, listId2.size)
        assertEquals("Item 4", listId2[0].name)
        assertEquals("Item 5", listId2[1].name)
    }
}