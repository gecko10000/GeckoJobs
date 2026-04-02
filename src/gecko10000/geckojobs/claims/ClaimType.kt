package gecko10000.geckojobs.claims

enum class ClaimType {
    BREWING_STAND,
    FURNACE,
    ;

    override fun toString(): String {
        return this.name.lowercase().replace('_', ' ')
    }
}
