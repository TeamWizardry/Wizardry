package com.teamwizardry.wizardry.common.entity;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.wizardry.common.core.FakeServerHandler;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class EntityStaffFakePlayer extends FakePlayer {

	private static GameProfile STAFF_PROFILE = new GameProfile(UUID.fromString("40c5bdaf-0ae1-42ec-8557-f7dc758c560d"), "A Pearl on a Staff");

	public EntityStaffFakePlayer(WorldServer world) {
		super(world, STAFF_PROFILE);

		connection = new FakeServerHandler(this);
		setSize(0.0F, 0.0F);
	}
}
