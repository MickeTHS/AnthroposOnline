package com.mnn.society.md3;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;

public class MaterialInfo {

        private String name;    // Identificação do material
        private Texture tex;
        private String texFile = "";
        private byte[] colorByte;
        private float[] colorConverted;
        private FloatBuffer ka; // Ambiente
        private FloatBuffer kd; // Difuso
        private FloatBuffer ks; // Especular
        private FloatBuffer ke; // Emissão
        private float spec;     // Fator de especularidade
        public static final int SIZE_FLOAT = 4;
        private int   texureId;                         // the texture ID
        private float uTile;                            // u tiling of texture  
        private float vTile;                            // v tiling of texture  
        private float uOffset;                      // u offset of texture
        private float vOffset;                          // v offset of texture


        
        public MaterialInfo()
        {
                setKa(new float[SIZE_FLOAT]);
                setKd(new float[SIZE_FLOAT]);
                setKs(new float[SIZE_FLOAT]);
                setKe(new float[SIZE_FLOAT]);
                setSpec(0.0f);
                setColor(new byte[3]);
                colorConverted = new float[3];
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         * @return the name
         */
        public String getName() {
                return name;
        }

        /**
         * @param ka the ka to set
         */
        public void setKa(float[] ka) {
                this.ka = BufferUtils.createFloatBuffer(ka.length);
                for(int i = 0; i < ka.length; i++) {
                	this.ka.put(ka[i]);
                }
        }

        /**
         * @return the ka
         */
        public FloatBuffer getKa() {
                
                return ka;
        }

        /**
         * @param kd the kd to set
         */
        public void setKd(float[] kd) {
                this.kd = BufferUtils.createFloatBuffer(kd.length);
                for(int i = 0; i < kd.length; i++) {
                	this.kd.put(kd[i]);
                }
        }

        /**
         * @return the kd
         */
        public FloatBuffer getKd() {
                
                return kd;
        }

        /**
         * @param ks the ks to set
         */
        public void setKs(float[] ks) {
                this.ks = BufferUtils.createFloatBuffer(ks.length);
                
                for(int i = 0; i < ks.length; i++) {
                	this.ks.put(ks[i]);
                }
        }

        /**
         * @return the ks
         */
        public FloatBuffer getKs() {
                
                return ks;
        }

        /**
         * @param ke the ke to set
         */
        public void setKe(float[] ke) {
                this.ke = BufferUtils.createFloatBuffer(ke.length);
                for(int i = 0; i < ke.length; i++) {
                	this.ke.put(ke[i]);
                }
        }
        
        public void setKe(float ke) {
                this.ke.put(0, ke);
                this.ke.put(1, ke);
                this.ke.put(2, ke);
                
        }

        /**
         * @return the ke
         */
        public FloatBuffer getKe() {
                
                return ke;
        }

        /**
         * @param spec the spec to set
         */
        public void setSpec(float spec) {
                this.spec = spec;
        }
        
        public void setAlpha(float spec)
        {
                ka.put(3, spec);
                kd.put(3, spec);
                ks.put(3, spec);
                ke.put(3, spec);
                
                
        }

        /**
         * @return the spec
         */
        public float getSpec() {
                return spec;
        }

        /**
         * @param tex the tex to set
         */
        public void setTex(Texture tex) {
                this.tex = tex;
        }

        /**
         * @return the tex
         */
        public Texture getTex() {
                return tex;
        }

        /**
         * @param texFile the texFile to set
         */
        public void setTexFile(String texFile) {
                this.texFile = texFile;
        }

        /**
         * @return the texFile
         */
        public String getTexFile() {
                return texFile;
        }

        /**
         * @param color the color to set
         */
        public void setColor(byte[] color) {
                this.colorByte = color;
        }
        
        /**
         * @param colorByte the color to set
         */
        public void setColor() {
                this.colorConverted[0] = colorByte[0] & 0xff;
                this.colorConverted[1] = colorByte[1] & 0xff;
                this.colorConverted[2] = colorByte[2] & 0xff;
        }
        
        

        /**
         * @return the color
         */
        public byte[] getColor() {
                
                return colorByte;
        }
        
        /**
         * @return the color
         */
        public float[] getColorConverted() 
        {
                setColor();
                this.colorConverted[0] /= 255;
                this.colorConverted[1] /= 255;
                this.colorConverted[2] /= 255;
                return colorConverted;
        }

        /**
         * @param texureId the texureId to set
         */
        public void setTexureId(int texureId) {
                this.texureId = texureId;
        }

        /**
         * @return the texureId
         */
        public int getTexureId() {
                return texureId;
        }

        /**
         * @param uTile the uTile to set
         */
        public void setUTile(float uTile) {
                this.uTile = uTile;
        }

        /**
         * @return the uTile
         */
        public float getUTile() {
                return uTile;
        }

        /**
         * @param vTile the vTile to set
         */
        public void setVTile(float vTile) {
                this.vTile = vTile;
        }

        /**
         * @return the vTile
         */
        public float getVTile() {
                return vTile;
        }

        /**
         * @param uOffset the uOffset to set
         */
        public void setUOffset(float uOffset) {
                this.uOffset = uOffset;
        }

        /**
         * @return the uOffset
         */
        public float getUOffset() {
                return uOffset;
        }

        /**
         * @param vOffset the vOffset to set
         */
        public void setVOffset(float vOffset) {
                this.vOffset = vOffset;
        }

        /**
         * @return the vOffset
         */
        public float getVOffset() {
                return vOffset;
        }

        
        
}