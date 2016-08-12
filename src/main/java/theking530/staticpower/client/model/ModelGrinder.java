package theking530.staticpower.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelGrinder extends ModelBase
{
  //fields
    ModelRenderer Blade_2;
    ModelRenderer Blade_1;
    ModelRenderer Shape1;
    ModelRenderer Blade_3;
    ModelRenderer Blade_4;
  
  public ModelGrinder()
  {
    textureWidth = 64;
    textureHeight = 64;
    
      Blade_2 = new ModelRenderer(this, 0, 0);
      Blade_2.addBox(-1.5F, -7F, -3.466667F, 3, 14, 1);
      Blade_2.setRotationPoint(0F, 16F, 0F);
      Blade_2.setTextureSize(64, 64);
      Blade_2.mirror = true;
      setRotation(Blade_2, 0F, 0F, 0.7853982F);
      Blade_1 = new ModelRenderer(this, 0, 0);
      Blade_1.addBox(-1.5F, -7F, -3.5F, 3, 14, 1);
      Blade_1.setRotationPoint(0F, 16F, 0F);
      Blade_1.setTextureSize(64, 64);
      Blade_1.mirror = true;
      setRotation(Blade_1, 0F, 0F, -0.7853982F);
      Shape1 = new ModelRenderer(this, 0, 15);
      Shape1.addBox(-1F, -1F, -8F, 2, 2, 16);
      Shape1.setRotationPoint(0F, 16F, 0F);
      Shape1.setTextureSize(64, 64);
      Shape1.mirror = true;
      setRotation(Shape1, 0F, 0F, 0F);
      Blade_3 = new ModelRenderer(this, 0, 0);
      Blade_3.addBox(-1.5F, -7F, 2.5F, 3, 14, 1);
      Blade_3.setRotationPoint(0F, 16F, 0F);
      Blade_3.setTextureSize(64, 64);
      Blade_3.mirror = true;
      setRotation(Blade_3, 0F, 0F, -0.7853982F);
      Blade_4 = new ModelRenderer(this, 0, 0);
      Blade_4.addBox(-1.5F, -7F, 2.5F, 3, 14, 1);
      Blade_4.setRotationPoint(0F, 16F, 0F);
      Blade_4.setTextureSize(64, 64);
      Blade_4.mirror = true;
      setRotation(Blade_4, 0F, 0F, 0.7853982F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    Blade_2.render(f5);
    Blade_1.render(f5);
    Shape1.render(f5);
    Blade_3.render(f5);
    Blade_3.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
