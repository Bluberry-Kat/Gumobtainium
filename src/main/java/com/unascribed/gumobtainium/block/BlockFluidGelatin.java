package com.unascribed.gumobtainium.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidGelatin extends BlockFluidClassic {

	public BlockFluidGelatin(Fluid fluid, Material material) {
		super(fluid, material);
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		IBlockState neighbor = world.getBlockState(pos.offset(side));
		if (neighbor.getBlock() == this) return false;
		if (side == EnumFacing.UP) return true;
		return !neighbor.doesSideBlockRendering(world, pos.offset(side), side.getOpposite());
	}
	
}
