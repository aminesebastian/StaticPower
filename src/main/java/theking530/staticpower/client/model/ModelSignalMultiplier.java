package theking530.staticpower.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSignalMultiplier extends ModelBase
{
  //fields
    ModelRenderer Base;
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
  
  public ModelSignalMultiplier()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      Base = new ModelRenderer(this, 0, 0);
      Base.addBox(-8F, 0F, -8F, 16, 2, 16);
      Base.setRotationPoint(0F, 22F, 0F);
      Base.setTextureSize(64, 32);
      setRotation(Base, 0F, 0F, 0F);
      Shape1 = new ModelRenderer(this, 0, 18);
      Shape1.addBox(0F, -0.5F, -4.5F, 1, 1, 9);
      Shape1.setRotationPoint(3.5F, 22F, 0F);
      Shape1.setTextureSize(64, 32);
      setRotation(Shape1, 0F, 0F, 0F);
      Shape2 = new ModelRenderer(this, 0, 18);
      Shape2.addBox(0F, -0.5F, -4.5F, 1, 1, 9);
      Shape2.setRotationPoint(-4.5F, 22F, 0F);
      Shape2.setTextureSize(64, 32);
      setRotation(Shape2, 0F, 0F, 0F);
      Shape3 = new ModelRenderer(this, 0, 18);
      Shape3.addBox(0F, -0.49F, -4.5F, 1, 1, 9);
      Shape3.setRotationPoint(0F, 22F, 5F);
      Shape3.setTextureSize(64, 32);
      setRotation(Shape3, 0F, 1.570796F, 0F);
      Shape4 = new ModelRenderer(this, 0, 18);
      Shape4.addBox(0F, -0.49F, -4.5F, 1, 1, 9);
      Shape4.setRotationPoint(0F, 22F, -4F);
      Shape4.setTextureSize(64, 32);
      setRotation(Shape4, 0F, 1.570796F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    Base.render(f5);
    Shape1.render(f5);
    Shape2.render(f5);
    Shape3.render(f5);
    Shape4.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f5, f, f1, f2, f3, f4, entity);
  }

}
