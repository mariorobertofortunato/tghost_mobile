package com.mrf.tghost.chain.evm.data.network.pagination

data class AlchemyCursorPage<T>(
    val items: List<T>,
    val nextCursor: String?,
)

suspend fun <T> paginateAlchemyByCursor(
    maxPages: Int = 10,
    fetchPage: suspend (cursor: String?) -> AlchemyCursorPage<T>,
): List<T> {
    val allItems = mutableListOf<T>()
    val seenCursors = mutableSetOf<String>()
    var cursor: String? = null
    var pageCount = 0

    while (pageCount < maxPages) {
        pageCount++
        val page = fetchPage(cursor)
        val pageItems = page.items
        allItems += pageItems

        val nextCursor = page.nextCursor?.takeIf { it.isNotBlank() } ?: break

        // Guard 1: prevent infinite loop when cursor does not advance.
        if (nextCursor == cursor) break
        // Guard 2: prevent cursor cycles.
        if (!seenCursors.add(nextCursor)) break
        // Guard 3: stop if API returns empty page while still advertising a cursor.
        if (pageItems.isEmpty()) break

        cursor = nextCursor
    }

    return allItems
}
