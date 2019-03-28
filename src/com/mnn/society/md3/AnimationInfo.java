package com.mnn.society.md3;

public class AnimationInfo {
        
        public String animName;                        // This stores the name of the animation (Jump, Pain, etc..)
        public int startFrame;                         // This stores the first frame number for this animation
        public int endFrame;                           // This stores the last frame number for this animation
        public int loopingFrames;
        public int framesPerSecond;
        
        public AnimationInfo() {
                setAnimName(null);
                setStartFrame(0);
                setEndFrame(0);
                setLoopingFrames(0);
                setFramesPerSecond(0);
        }

        public AnimationInfo(String animFrame, int startFrame, int endFrame) {
                setAnimName(animFrame);
                setStartFrame(startFrame);
                setEndFrame(endFrame);
        }
        /**
         * @param animName the animName to set
         */
        public void setAnimName(String animName) {
                this.animName = animName;
        }

        /**
         * @return the animName
         */
        public String getAnimName() {
                return animName;
        }

        /**
         * @param startFrame the startFrame to set
         */
        public void setStartFrame(int startFrame) {
                this.startFrame = startFrame;
        }
       
        /**
         * @param endFrame the endFrame to set
         */
        public void setEndFrame(int endFrame) {
                this.endFrame = endFrame;
        }
      
        /**
         * @param loopingFrames the loopingFrames to set
         */
        public void setLoopingFrames(int loopingFrames) {
                this.loopingFrames = loopingFrames;
        }

        /**
         * @return the loopingFrames
         */
        public int getLoopingFrames() {
                return loopingFrames;
        }

        /**
         * @param framesPerSecond the framesPerSecond to set
         */
        public void setFramesPerSecond(int framesPerSecond) {
                this.framesPerSecond = framesPerSecond;
        }

        /**
         * @return the framesPerSecond
         */
        public int getFramesPerSecond() {
                return framesPerSecond;
        }
}