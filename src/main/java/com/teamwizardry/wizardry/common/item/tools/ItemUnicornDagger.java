package com.teamwizardry.wizardry.common.item.tools;

import com.google.common.collect.Multimap;
import com.teamwizardry.librarianlib.features.base.item.ItemModSword;
import com.teamwizardry.wizardry.init.ModItems;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemUnicornDagger extends ItemModSword
{
	public ItemUnicornDagger()
	{
		super("unicorn_dagger", ModItems.UNICORN_HORN_MAT);
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
		
		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			modifiers.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack), 0));
		}
		
		return modifiers;
	}
	
	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event)
	{
		if (event.getTarget().canBeAttackedWithItem() && !event.getTarget().hitByEntity(event.getEntity()) && event.getTarget() instanceof EntityLivingBase && event.getTarget().hurtResistantTime <= 0)
		{
			EntityLivingBase attacker = event.getEntityLiving();
			EntityLivingBase target = (EntityLivingBase) event.getTarget();
			
			if (attacker.getHeldItemMainhand() == ItemStack.EMPTY)
				return;
			if (attacker.getHeldItemMainhand().getItem() != ModItems.UNICORN_DAGGER)
				return;
			
			float damage = 1 + EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, attacker.getHeldItemMainhand());
			float attackCD = attacker instanceof EntityPlayer ? ((EntityPlayer) attacker).getCooledAttackStrength(0.5F) : 1;
			damage *= (0.2f + attackCD * attackCD * 0.8f);
			
			target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(attacker, null), damage);
			target.hurtResistantTime = 0;
		}
	}
}
