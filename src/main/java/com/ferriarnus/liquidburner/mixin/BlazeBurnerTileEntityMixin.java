package com.ferriarnus.liquidburner.mixin;

import com.ferriarnus.liquidburner.BlazeTank;
import com.ferriarnus.liquidburner.Tags;
import com.ferriarnus.liquidburner.recipe.FluidContainer;
import com.ferriarnus.liquidburner.recipe.LiquidBurning;
import com.ferriarnus.liquidburner.recipe.RecipeRegistry;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BlazeBurnerBlockEntity.class)
public abstract class BlazeBurnerTileEntityMixin extends SmartBlockEntity {
    @Unique
    BlazeTank tank = new BlazeTank(1000, fluidStack -> tryConsumeLiquid(), fluid -> fluid.getFluid().is(Tags.Fuilds.BLAZE_BURNER_FUEL_ALL));
    @Unique
    LazyOptional<IFluidHandler> lazy = LazyOptional.of(() -> tank);
    @Shadow
    protected BlazeBurnerBlockEntity.FuelType activeFuel;
    @Shadow
    protected int remainingBurnTime;
    private LiquidBurning lb;

    public BlazeBurnerTileEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private void tryConsumeLiquid() {
        if (this.tank.getFluidAmount() == this.tank.getCapacity()) {
            if (recipeFluids()) return;
            BlazeBurnerBlockEntity.FuelType newFuel = BlazeBurnerBlockEntity.FuelType.NONE;
            int newBurnTime = 0;
            if (this.tank.getFluid().getFluid().is(Tags.Fuilds.BLAZE_BURNER_FUEL_SPECIAL)) {
                newBurnTime = 1000;
                newFuel = BlazeBurnerBlockEntity.FuelType.SPECIAL;
            } else if (this.tank.getFluid().getFluid().is(Tags.Fuilds.BLAZE_BURNER_FUEL_REGULAR)) {
                newBurnTime = 1600;
                newFuel = BlazeBurnerBlockEntity.FuelType.NORMAL;
            }

            if (newFuel == BlazeBurnerBlockEntity.FuelType.NONE) {
                return;
            } else if (newFuel.ordinal() < this.activeFuel.ordinal()) {
                return;
            } else if (this.activeFuel == BlazeBurnerBlockEntity.FuelType.SPECIAL && this.remainingBurnTime > 20) {
                return;
            } else {
                if (newFuel == this.activeFuel) {
                    if (this.remainingBurnTime + newBurnTime > 10000) {
                        return;
                    }

                    newBurnTime = Mth.clamp(this.remainingBurnTime + newBurnTime, 0, 10000);
                }

                this.activeFuel = newFuel;
                this.remainingBurnTime = newBurnTime;
                if (this.level.isClientSide) {
                    this.spawnParticleBurst(this.activeFuel == BlazeBurnerBlockEntity.FuelType.SPECIAL);
                    tank.drain(tank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    return;
                } else {
                    BlazeBurnerBlock.HeatLevel prev = this.getHeatLevelFromBlock();
                    this.playSound();
                    this.updateBlockState();
                    if (prev != this.getHeatLevelFromBlock()) {
                        this.level.playSound((Player)null, this.getBlockPos(), SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS, 0.125F + this.level.random.nextFloat() * 0.125F, 1.15F - this.level.random.nextFloat() * 0.25F);
                    }
                    tank.drain(tank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    return;
                }
            }
        }
    }

    public boolean recipeFluids() {
        FluidContainer container = new FluidContainer(this.tank.getFluid());
        Optional<LiquidBurning> recipe = this.level.getRecipeManager().getRecipeFor(RecipeRegistry.LIQUIDBURNING.get(), container, this.level);
        if (recipe.isPresent()) {
            lb = recipe.get();
            BlazeBurnerBlockEntity.FuelType newFuel;
            int newBurnTime;
            if (lb.getSuperheattime() > 0) {
                newBurnTime = lb.getSuperheattime();
                newFuel = BlazeBurnerBlockEntity.FuelType.SPECIAL;
            } else {
                newBurnTime = lb.getBurntime();
                newFuel = BlazeBurnerBlockEntity.FuelType.NORMAL;
                lb = null;
            }

            if (newFuel.ordinal() < this.activeFuel.ordinal()) {
                return true;
            } else if (this.activeFuel == BlazeBurnerBlockEntity.FuelType.SPECIAL && this.remainingBurnTime > 20) {
                return true;
            } else {
                if (newFuel == this.activeFuel) {
                    if (this.remainingBurnTime + newBurnTime > 10000) {
                        return false;
                    }

                    newBurnTime = Mth.clamp(this.remainingBurnTime + newBurnTime, 0, 10000);
                }
                this.activeFuel = newFuel;
                this.remainingBurnTime = newBurnTime;
                if (this.level.isClientSide) {
                    this.spawnParticleBurst(this.activeFuel == BlazeBurnerBlockEntity.FuelType.SPECIAL);
                    tank.drain(tank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    return true;
                } else {
                    BlazeBurnerBlock.HeatLevel prev = this.getHeatLevelFromBlock();
                    this.playSound();
                    this.updateBlockState();
                    if (prev != this.getHeatLevelFromBlock()) {
                        this.level.playSound((Player)null, this.getBlockPos(), SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS, 0.125F + this.level.random.nextFloat() * 0.125F, 1.15F - this.level.random.nextFloat() * 0.25F);
                    }
                    tank.drain(tank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    return true;

                }
            }
        }
        lb = null;
        return false;
    }
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 5000), remap = false)
    public int liquidburner$addBurntime(int constant) {
        if (lb != null) {
            int temp = lb.getBurntime();
            lb = null;
            return temp;
        }
        return constant;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlockEntity;updateBlockState()V", shift = At.Shift.BEFORE, ordinal = 1), method = "tick", remap = false)
    public void liquidburner$appendTick(CallbackInfo ci) {
        if (this.remainingBurnTime <= 0) {
            this.tryConsumeLiquid();
        }
    }

    @Inject(at = @At("HEAD"), method = "read", remap = false)
    public void liquidburner$appendRead(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        this.tank.readFromNBT(compound.getCompound("tank"));
    }

    @Inject(at = @At("HEAD"), method = "write", remap = false)
    public void liquidburner$appendWrite(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        CompoundTag tag = new CompoundTag();
        this.tank.writeToNBT(tag);
        compound.put("tank", tag);
    }

    @Shadow
    public abstract void updateBlockState();

    @Shadow
    protected abstract void playSound();

    @Shadow
    public abstract BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock();

    @Shadow
    public abstract void spawnParticleBurst(boolean b);

    @Shadow public abstract void tick();

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazy.cast();
        }
        return super.getCapability(cap, side);
    }
}
