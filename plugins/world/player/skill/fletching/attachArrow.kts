import api.*
import io.luna.game.action.Action
import io.luna.game.action.ProducingAction
import io.luna.game.event.impl.ItemOnItemEvent
import io.luna.game.model.item.Item
import io.luna.game.model.mob.Player
import io.luna.game.model.mob.dialogue.MakeItemDialogueInterface
import world.player.skill.fletching.Arrow

/**
 * A [ProducingAction] that will attach arrowtips to headless arrows.
 */
class MakeArrowAction(plr: Player,
                      private val arrow: Arrow,
                      private var makeTimes: Int) : ProducingAction(plr, true, 3) {

    /**
     * The fletching skill.
     */
    private val fletching = mob.skill(SKILL_FLETCHING)!!

    /**
     * The amount of arrows to make in this set.
     */
    private var setAmount = 0

    override fun add() = arrayOf(Item(arrow.arrow, setAmount))
    override fun remove(): Array<Item> {
        val tipItem = Item(arrow.tip, setAmount)
        val withItem = Item(arrow.with, setAmount)
        return arrayOf(tipItem, withItem)
    }

    override fun canProduce(): Boolean {
        return when {

            // Check fletching level.
            fletching.level < arrow.level -> {
                mob.sendMessage("You need a Fletching level of ${arrow.level} to attach this.")
                false
            }

            // No more actions to execute.
            makeTimes == 0 -> false

            // Check if there's enough materials.
            else -> {
                val withCount = mob.inventory.computeAmountForId(arrow.with)
                val tipCount = mob.inventory.computeAmountForId(arrow.tip)
                setAmount = Integer.min(withCount, tipCount)
                setAmount = Integer.min(setAmount, Arrow.SET_AMOUNT)

                setAmount != 0
            }
        }
    }

    override fun onProduce() {
        val withName = itemDef(arrow.with)?.name
        val tipName = itemDef(arrow.tip)?.name
        mob.sendMessage("You attach the $tipName to the $withName.")

        fletching.addExperience(arrow.exp * setAmount)
        makeTimes--
    }

    override fun isEqual(other: Action<*>) =
        when (other) {
            is MakeArrowAction -> arrow == other.arrow
            else -> false
        }
}

/**
 * Opens a [MakeItemDialogueInterface] for making arrows.
 */
fun openInterface(msg: ItemOnItemEvent, arrow: Arrow?) {
    if (arrow != null) {
        val plr = msg.plr
        plr.interfaces.open(object : MakeItemDialogueInterface(arrow.arrow) {
            override fun makeItem(player: Player, id: Int, index: Int, forAmount: Int) =
                plr.submitAction(MakeArrowAction(plr, arrow, forAmount))
        })
    }
}

/**
 * Intercept item on item event to open interface.
 */
on(ItemOnItemEvent::class)
    .condition { it.targetId == Arrow.HEADLESS || it.usedId == Arrow.HEADLESS }
    .run {
        when (Arrow.HEADLESS) {
            it.targetId -> openInterface(it, Arrow.TIP_TO_ARROW[it.usedId])
            it.usedId -> openInterface(it, Arrow.TIP_TO_ARROW[it.targetId])
        }
    }

/**
 * Intercept even specifically for [Arrow.HEADLESS_ARROW], because it differs from others.
 */
Arrow.HEADLESS_ARROW.apply {
    on(ItemOnItemEvent::class)
        .args(tip, with)
        .run { openInterface(it, this) }
}