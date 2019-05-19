package columbia;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FilterCommentsDriver {
    public static void main(String args[]) throws Exception {
        // Argument check
        if(args.length < 2) {
            System.err.println("Must provide an input path and an output path!");
            System.exit(1);
        }

        // Paths
        String inFile = args[0];
        String outFile = args[1];

        Configuration conf = new Configuration();

        Job filterJob = Job.getInstance(conf, "Filter Comments");
        filterJob.setJarByClass(FilterCommentsDriver.class);
        filterJob.setMapperClass(RelevantFilterMapper.class);
        filterJob.setReducerClass(Reducer.class); // Identity reducer
        filterJob.setOutputKeyClass(Text.class);
        filterJob.setOutputValueClass(Text.class);
        filterJob.setNumReduceTasks(16);

        // Input and output paths
        FileInputFormat.addInputPath(filterJob, new Path(inFile));
        FileOutputFormat.setOutputPath(filterJob, new Path(outFile));

        // Run job
        if(!filterJob.waitForCompletion(true)) { System.exit(1); }
    }
}
