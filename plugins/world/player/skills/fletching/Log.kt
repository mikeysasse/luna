package world.player.skills.fletching

import world.player.skills.fletching.Bow.*

/**
 * An enum representing an item that can be cut into a [Bow].
 */
enum class Log(val id: Int, val bows: List<Bow>) {

    NORMAL(id = 1511,
           bows = listOf(ARROW_SHAFT, SHORTBOW, LONGBOW)),
    OAK(id = 1521,
        bows = listOf(OAK_SHORTBOW, OAK_LONGBOW)),
    WILLOW(id = 1519,
           bows = listOf(WILLOW_SHORTBOW, WILLOW_LONGBOW)),
    MAPLE(id = 1517,
          bows = listOf(MAPLE_SHORTBOW, MAPLE_LONGBOW)),
    YEW(id = 1515,
        bows = listOf(YEW_SHORTBOW, YEW_LONGBOW)),
    MAGIC(id = 1513,
          bows = listOf(MAGIC_SHORTBOW, MAGIC_LONGBOW));

    companion object {

        /**
         * The knife identifier.
         */
        const val KNIFE = 946

        /**
         * Mappings of [Log.id] to [Log] instances.
         */
        val ID_TO_LOG = values().map { it.id to it }.toMap()
    }

    /**
     * An array of unstrung identifiers made from this log.
     */
    val unstrungIds = bows.map { it.unstrung }.toIntArray()
}