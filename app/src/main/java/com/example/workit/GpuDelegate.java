package com.example.workit;

import java.io.Closeable;
import org.tensorflow.lite.Delegate;
import org.tensorflow.lite.annotations.UsedByReflection;

@UsedByReflection("TFLiteSupport/model/GpuDelegateProxy")
public class GpuDelegate implements Delegate, Closeable {
    private static final long INVALID_DELEGATE_HANDLE = 0L;
    private static final String TFLITE_GPU_LIB = "tensorflowlite_gpu_jni";
    private long delegateHandle;

    public GpuDelegate(GpuDelegate.Options options) {
        this.delegateHandle = createDelegate(options.precisionLossAllowed, options.quantizedModelsAllowed, options.inferencePreference);
    }

    @UsedByReflection("TFLiteSupport/model/GpuDelegateProxy")
    public GpuDelegate() {
        this(new GpuDelegate.Options());
    }

    public long getNativeHandle() {
        return this.delegateHandle;
    }

    public void close() {
        if (this.delegateHandle != 0L) {
            deleteDelegate(this.delegateHandle);
            this.delegateHandle = 0L;
        }

    }

    private static native long createDelegate(boolean var0, boolean var1, int var2);

    private static native void deleteDelegate(long var0);

    static {
        System.loadLibrary("tensorflowlite_gpu_jni");
    }

    public static final class Options {
        public static final int INFERENCE_PREFERENCE_FAST_SINGLE_ANSWER = 0;
        public static final int INFERENCE_PREFERENCE_SUSTAINED_SPEED = 1;
        boolean precisionLossAllowed = true;
        boolean quantizedModelsAllowed = false;
        int inferencePreference = 0;

        public Options() {
        }

        public GpuDelegate.Options setPrecisionLossAllowed(boolean precisionLossAllowed) {
            this.precisionLossAllowed = precisionLossAllowed;
            return this;
        }

        public GpuDelegate.Options setQuantizedModelsAllowed(boolean quantizedModelsAllowed) {
            this.quantizedModelsAllowed = quantizedModelsAllowed;
            return this;
        }

        public GpuDelegate.Options setInferencePreference(int preference) {
            this.inferencePreference = preference;
            return this;
        }
    }
}
