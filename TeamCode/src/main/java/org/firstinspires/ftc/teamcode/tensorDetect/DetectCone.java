/*package org.firstinspires.ftc.teamcode.tensorDetect;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.InterpreterApi;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.support.model.Model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;


public class DetectCone {

    private static final String INPUT_NAME = "image_tensor";
    private static final int MAX_RESULTS = 1;

    private Graph graph;

    Interpreter interpreter = new Interpreter(file_of_a_tensorflowlite_model);

    public DetectCone(File graphFile, File labelFile) throws IOException {
        InputStream graphInputStream = Files.newInputStream(graphFile.toPath());
        List<String> labels = loadLabels(labelFile);
        setup(graphInputStream, labels);
    }

    public DetectCone(File graphFile, File labelFile) throws IOException {
        InputStream graphInputStream = Files.newInputStream(graphFile.toPath());
        InputStream

    }


    // Init interpreter with GPU delegate
    MyClassifierModel myImageClassifier = null;

    CompatibilityList compatList = CompatibilityList();
    private Model.Options options = compatList.isDelegateSupportedOnThisDevice()
            ? Model.Options.Builder().setDevice(Model.Device.GPU).build()
            : Model.Options.Builder().setNumThreads(4).build();
    MyModel myModel = new MyModel.newInstance(context, options);

}


*/







/*


public class DetectCone {

    private static final String INPUT_NAME = "image_tensor";
    private static final int MAX_RESULTS = 1;

    // the 'Graph' class doesn't seem to be in Tensorflow Lite, so this will have to do
    private Graph graph;
    private byte[] graphBytes;
    private List<String> labels;

    public DetectCone(File graphFile, File labelFile) throws IOException {
        DependencyHandler.loadDependencies();

        InputStream graphInputStream = Files.newInputStream(graphFile.toPath());
        InputStream labelInputStream = Files.newInputStream(labelFile.toPath());

        List<String> labels = loadLabels(labelInputStream);
        setup(graphInputStream, labels);


    }

    public DetectCone(InputStream graphFile, List<String> labels) throws IOException {
        setup(graphFile, labels);
    }

    private void setup(InputStream graphFile, List<String> labels) throws IOException {
        byte[] graphBytes = loadGraph(graphFile);
        //graph bytes must be an input stream???
        this.graph = loadGraph(graphBytes);
        this.labels = labels;
    }

    private byte[] loadGraph(InputStream graphFile) throws IOException {

        int baosInitSize = graphFile.available() > 16384 ? graphFile.available() : 16384;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(baosInitSize);
        int numBytesRead;
        byte[] buf = new byte[16384];
        while ((numBytesRead = graphFile.read(buf, 0, buf.length)) != -1) {
            baos.write(buf, 0, numBytesRead);
        }

        return baos.toByteArray();
    }

    private List<String> loadLabels(File labelFile) throws IOException {
        List<String> labels = new ArrayList<>(2);
        List<String> fileLines = Files.readAllLines(labelFile.toPath());

        for (String line : fileLines) {
            if(line.contains("name:")) {
                int i = line.indexOf("'");
                String substring = line.substring(i + 1, line.length() - 1);
                labels.add(substring);
            }
        }

        return labels;
    }

    @Override
    public ArrayList<ObjectRecognition> classifyImage(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        Tensor<UInt8> imageTensor = normalizeImage_UInt8(image);

        Detection detection = executeGraph(imageTensor);

        ArrayList<ObjectRecognition> objectRecognitions = processDetections(detection, width, height);

        imageTensor.close();

        return objectRecognitions;
    }

    public Tensor<UInt8> normalizeImage_UInt8(BufferedImage image) {
//        int[] imageInts = new int[width * height];
//        byte[] byteValues = new byte[width * height * 3];

//        image.getRGB(0,0, image.getWidth(), image.getHeight(), imageInts, 0, image.getWidth());

//        for (int i = 0; i < imageInts.length; ++i) {
//            byteValues[i * 3 + 2] = (byte) (imageInts[i] & 0xFF);
//            byteValues[i * 3 + 1] = (byte) ((imageInts[i] >> 8) & 0xFF);
//            byteValues[i * 3 + 0] = (byte) ((imageInts[i] >> 16) & 0xFF);
//        }

//        if (image.getType() != BufferedImage.TYPE_3BYTE_BGR) {
//            throw new RuntimeException("Expected 3-byte BGR encoding in BufferedImage, found " + image.getType());
//        }

        byte[] data = ((DataBufferByte) image.getData().getDataBuffer()).getData();
        bgr2rgb(data);

        final long BATCH_SIZE = 1;
        final long CHANNELS = 3;
        long[] shape = new long[] {BATCH_SIZE, image.getHeight(), image.getWidth(), CHANNELS};
        return Tensor.create(UInt8.class, shape, ByteBuffer.wrap(data));
    }
    private static void bgr2rgb(byte[] data) {
        for (int i = 0; i < data.length; i += 3) {
            byte tmp = data[i];
            data[i] = data[i + 2];
            data[i + 2] = tmp;
        }
    }
    private Detection executeGraph(final Tensor<?> image) {

        try(CustomGraphProcessor classifier = new CustomGraphProcessor(this.graph)) {
            classifier.feed(INPUT_NAME, image);
            classifier.run();

//            float[] num_detections = classifier.get_num_detections();
//            float[] detection_boxes = classifier.get_detection_boxes();
//            float[] detection_scores = classifier.get_detection_scores();
//            float[] detection_classes = classifier.get_detection_classes();
//
//            ClassifyRecognition detection = new ClassifyRecognition(num_detections, detection_boxes, detection_scores, detection_classes);
//
//            System.out.println(num_detections);

            return classifier.detections();
        }
    }

    private Graph loadGraph(byte[] graphBytes) {
        Graph graph = new Graph();
        graph.importGraphDef(graphBytes);
        return graph;
    }

    private ArrayList<ObjectRecognition> processDetections(Detection detection, int width, int height) {
        // Find the best detections.
        final PriorityQueue<ObjectRecognition> priorityQueue =
                new PriorityQueue<ObjectRecognition>(1, new RecognitionComparator());

        float[] detection_boxes = detection.getDetection_boxes();
        float[] detection_scores = detection.getDetection_scores();
        float[] detection_classes = detection.getDetection_classes();

        // Scale them back to the input size.
        for (int i = 0; i < detection_scores.length; ++i) {
            final RectFloats rectDetection =
                    new RectFloats(
                            detection_boxes[4 * i + 1] * width,
                            detection_boxes[4 * i] * height,
                            detection_boxes[4 * i + 3] * width,
                            detection_boxes[4 * i + 2] * height);
            priorityQueue.add(
                    new ObjectRecognition("" + i, labels.get(( (int) detection_classes[i] ) - 1), detection_scores[i], rectDetection));
        }

        final ArrayList<ObjectRecognition> objectRecognitions = new ArrayList<>();
        for (int i = 0; i < Math.min(priorityQueue.size(), MAX_RESULTS); ++i) {
            objectRecognitions.add(priorityQueue.poll());
        }

        return objectRecognitions;
    }

    @Override
    public void close() throws Exception {
        this.graph.close();
    }

    class RecognitionComparator implements Comparator<ObjectRecognition> {
        @Override
        public int compare(final ObjectRecognition objectRecognitionA, final ObjectRecognition objectRecognitionB) {
            return Float.compare(objectRecognitionB.getConfidence(), objectRecognitionA.getConfidence());
        }
    }


}*/