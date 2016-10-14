package exnihiloadscensio.entities;

import exnihiloadscensio.items.ENItems;
import exnihiloadscensio.items.ItemResource;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ProjectileStone extends EntityThrowable
{
    public ProjectileStone(World worldIn)
    {
        super(worldIn);
    }
    
    public ProjectileStone(World worldIn, EntityLivingBase thrower)
    {
        super(worldIn, thrower);
    }
    
    public ProjectileStone(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }
    
    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result.entityHit != null)
        {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (int) (Math.random() * (4.0F / 3.0F)));
        }
        
        for (int j = 0; j < 8; ++j)
        {
            this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new int[] { Block.getStateId(Blocks.STONE.getDefaultState()) });
        }
        
        if (!this.worldObj.isRemote)
        {
            this.setDead();
            this.worldObj.spawnEntityInWorld(new EntityItem(worldObj, lastTickPosX, lastTickPosY, lastTickPosZ, ItemResource.getResourceStack(ItemResource.STONES)));
        }
    }
}