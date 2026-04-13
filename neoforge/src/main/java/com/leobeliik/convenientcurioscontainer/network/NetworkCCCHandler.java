package com.leobeliik.convenientcurioscontainer.network;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import com.leobeliik.convenientcurioscontainer.common.ConvenientMenuProvider;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

class NetworkCCCHandler {
	private static final NetworkCCCHandler INSTANCE = new NetworkCCCHandler();

	static NetworkCCCHandler getInstance() {
		return INSTANCE;
	}


	void handleSwitch(final SwitchCCC data, final IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			ServerPlayer player = (ServerPlayer) ctx.player();
			ItemStack offItem = player.getItemInHand(InteractionHand.OFF_HAND);
			ItemStack onItem = player.getItemInHand(InteractionHand.MAIN_HAND);

			if (onItem.getItem() instanceof ConvenientItem) {
				interact(player, onItem, data.open()); //check main hand first in case there's more than 1 item and it's earlier in the loop
			} else if (offItem.getItem() instanceof ConvenientItem) {
				interact(player, offItem, data.open());
			} else {
				player.getInventory().getNonEquipmentItems().stream().filter(itemStack -> itemStack.getItem() instanceof ConvenientItem)
						.findFirst().ifPresent(itemStack -> interact(player, itemStack, data.open()));
			}
		});
	}

	private void interact(ServerPlayer player, ItemStack stack, boolean open) {
		if (open) {
			//set item sprite to open
			ConvenientItem.setModel(stack, true);
			player.openMenu(ConvenientMenuProvider.MenuProvider(stack));
		}
	}


	void handlePageChange(final PageChange data, final IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			Player player = ctx.player();
			AbstractContainerMenu menu = player.containerMenu;

			if (menu instanceof ConvenientMenu) {
				((ConvenientMenu) menu).ChangePage(data.next());
			}
		});
	}

	void handleSlotChanged(final SlotChanged data, final IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			Player player = ctx.player();
			AbstractContainerMenu container = player.containerMenu;

			if (container instanceof ConvenientMenu menu) {
				menu.addSlots();
			}
		});
	}
}
