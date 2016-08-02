package com.teamwizardry.wizardry.client.fx;

import com.teamwizardry.librarianlib.fx.shader.Shader;
import com.teamwizardry.librarianlib.fx.shader.ShaderHelper;
import com.teamwizardry.librarianlib.fx.shader.uniforms.FloatTypes;
import com.teamwizardry.librarianlib.fx.shader.uniforms.IntTypes;

public class Shaders {

	public static BurstShader burst;
	public static Shader rawColor;
	
	static {
		burst = ShaderHelper.addShader(new BurstShader(null, "/assets/wizardry/shader/sparkle.frag"));
		rawColor = ShaderHelper.addShader(new Shader(null, "/assets/wizardry/shader/rawColor.frag"));
	}
	
	public static class BurstShader extends Shader {
		
		public FloatTypes.Float fanSpeedMin, fanSpeedMax, fanSizeMin, fanSizeMax, fanJitterMin, fanJitterMax;
		public IntTypes.Int fanBladesMin, fanBladesMax, fanCount;

	    public BurstShader(String vert, String frag) {
	        super(vert, frag);
	    }
	    
	    @Override
	    public void initUniforms() {
	    	fanCount = getUniform("fanCount");
	    	fanSpeedMin = getUniform("fanSpeedMin");
	    	fanSpeedMax = getUniform("fanSpeedMax");
	    	fanSizeMin = getUniform("fanSizeMin");
	    	fanSizeMax = getUniform("fanSizeMax");
	    	fanJitterMin = getUniform("fanJitterMin");
	    	fanJitterMax = getUniform("fanJitterMax");
	    	fanBladesMin = getUniform("fanBladesMin");
	    	fanBladesMax = getUniform("fanBladesMax");
	    }

		@Override
		public void uniformDefaults() {
			if(fanCount != null) fanCount.set(8);
			if(fanSpeedMax != null) fanSpeedMax.set( 0.7);
			if(fanSpeedMin != null) fanSpeedMin.set(-0.7);
			
			if(fanSizeMin != null) fanSizeMin.set(0.7);
			if(fanSizeMax != null) fanSizeMax.set(1.0);
			
			if(fanJitterMin != null) fanJitterMin.set(-0.3);
			if(fanJitterMax != null) fanJitterMax.set( 0.0);
			
			if(fanBladesMin != null) fanBladesMin.set(5);
			if(fanBladesMax != null) fanBladesMax.set(8);
		}
	}
	
}
