package com.example.iossearch.data

/** A single recent search entry, newest first. */
data class RecentSearch(
    val id: Long,
    val query: String,
    val timestamp: Long
)

/** A live suggestion shown as the user types, mirroring iOS Spotlight-style rows. */
data class SearchSuggestion(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val category: SuggestionCategory = SuggestionCategory.WEB,
    val iconName: String = "magnifyingglass"
)

enum class SuggestionCategory(val label: String) {
    TOP_HIT("Top Hit"),
    WEB("Web Search"),
    APP("Applications"),
    CONTACT("Contacts"),
    SETTINGS("Settings")
}

/** In-memory dataset the suggestion engine matches against — stands in for a real index/API. */
object SearchCorpus {
    val items: List<SearchSuggestion> = listOf(
        SearchSuggestion("1", "Weather", "App", SuggestionCategory.APP, "cloud.sun"),
        SearchSuggestion("2", "Messages", "App", SuggestionCategory.APP, "message"),
        SearchSuggestion("3", "Settings > Wi-Fi", "Settings", SuggestionCategory.SETTINGS, "wifi"),
        SearchSuggestion("4", "Settings > Bluetooth", "Settings", SuggestionCategory.SETTINGS, "dot.radiowaves.left.and.right"),
        SearchSuggestion("5", "Wikipedia: Android (operating system)", "Web", SuggestionCategory.WEB, "globe"),
        SearchSuggestion("6", "Wikipedia: Kotlin (programming language)", "Web", SuggestionCategory.WEB, "globe"),
        SearchSuggestion("7", "Sarah Chen", "Contact", SuggestionCategory.CONTACT, "person.crop.circle"),
        SearchSuggestion("8", "Sarah Johnson", "Contact", SuggestionCategory.CONTACT, "person.crop.circle"),
        SearchSuggestion("9", "Camera", "App", SuggestionCategory.APP, "camera"),
        SearchSuggestion("10", "Calculator", "App", SuggestionCategory.APP, "plus.slash.minus"),
        SearchSuggestion("11", "Calendar", "App", SuggestionCategory.APP, "calendar"),
        SearchSuggestion("12", "Search the web for \"{query}\"", "Web Search", SuggestionCategory.WEB, "magnifyingglass"),
        SearchSuggestion("13", "Notes", "App", SuggestionCategory.APP, "note.text"),
        SearchSuggestion("14", "News", "App", SuggestionCategory.APP, "newspaper"),
        SearchSuggestion("15", "Nearby restaurants", "Maps", SuggestionCategory.WEB, "map"),
        SearchSuggestion("16", "Settings > Battery", "Settings", SuggestionCategory.SETTINGS, "battery.100"),
        SearchSuggestion("17", "Podcasts", "App", SuggestionCategory.APP, "mic"),
        SearchSuggestion("18", "Photos", "App", SuggestionCategory.APP, "photo"),
        SearchSuggestion("19", "Reminders", "App", SuggestionCategory.APP, "checklist"),
        SearchSuggestion("20", "Translate", "App", SuggestionCategory.APP, "character.bubble")
    )

    fun query(text: String): List<SearchSuggestion> {
        if (text.isBlank()) return emptyList()
        val lower = text.trim().lowercase()
        val matches = items.filter { it.title.lowercase().contains(lower) && it.category != SuggestionCategory.WEB || it.title.lowercase().contains(lower) }
            .distinctBy { it.id }
            .filter { !it.title.contains("{query}") }

        val webFallback = SearchSuggestion(
            id = "web-fallback",
            title = "Search the web for \u201c${text.trim()}\u201d",
            subtitle = null,
            category = SuggestionCategory.WEB,
            iconName = "magnifyingglass"
        )

        val ranked = matches.sortedWith(
            compareBy(
                { !it.title.lowercase().startsWith(lower) },
                { it.title.length }
            )
        )

        return (ranked + webFallback).take(8)
    }
}
