package theking530.staticpower.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTank extends ModelBase
{
  //fields
    ModelRenderer Bottom;
    ModelRenderer Top;
    ModelRenderer TopMini;
    ModelRenderer BottomMini;
    ModelRenderer Shape1;
  
  public ModelTank()
  {
    textureWidth = 64;
    textureHeight = 64;
    
      Bottom = new ModelRenderer(this, 0, 24);
      Bottom.addBox(-8F, 0F, -8F, 16, 2, 16);
      Bottom.setRotationPoint(0F, 22F, 0F);
      Bottom.setTextureSize(64, 64);
      Bottom.mirror = true;
      setRotation(Bottom, 0F, 0F, 0F);
      Top = new ModelRenderer(this, 0, 24);
      Top.addBox(-8F, 0F, -8F, 16, 2, 16);
      Top.setRotationPoint(0F, 8F, 0F);
      Top.setTextureSize(64, 64);
      Top.mirror = true;
      setRotation(Top, 0F, 0F, 0F);
      TopMini = new ModelRenderer(this, 0, 42);
      TopMini.addBox(-7.5F, 0F, -7.5F, 15, 1, 15);
      TopMini.setRotationPoint(0F, 10F, 0F);
      TopMini.setTextureSize(64, 64);
      TopMini.mirror = true;
      setRotation(TopMini, 0F, 0F, 0F);
      BottomMini = new ModelRenderer(this, 0, 42);
      BottomMini.addBox(-7.5F, 0F, -7.5F, 15, 1, 15);
      BottomMini.setRotationPoint(0F, 21F, 0F);
      BottomMini.setTextureSize(64, 64);
      BottomMini.mirror = true;
      setRotation(BottomMini, 0F, 0F, 0F);
      Shape1 = new ModelRenderer(this, 0, 0);
      Shape1.addBox(0F, 0F, 0F, 13, 11, 13);
      Shape1.setRotationPoint(-6.5F, 10.5F, -6.5F);
      Shape1.setTextureSize(64, 64);
      Shape1.mirror = true;
      setRotation(Shape1, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    Bottom.render(f5);
    Top.render(f5);
    TopMini.render(f5);
    BottomMini.render(f5);
    Shape1.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
