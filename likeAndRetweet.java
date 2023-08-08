import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class likeAndRetweet {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable>{

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String line = value.toString();
            String[] splitted = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            if (!splitted[0].equals("created_at")) {
            splitted[2]=splitted[2].toLowerCase();
            String[] trumpMatches = new String[] {".*\\#donaldtrump\\b.*",".*\\#trump\\b.*"};
            String[] bidenMatches = new String[] {".*\\#joebiden\\b.*",".*\\#biden\\b.*"};
            
            int likesNum=(int)Float.parseFloat(splitted[3]);
            int retweetsNum=(int)Float.parseFloat(splitted[4]);
            IntWritable likes=new IntWritable(likesNum);
            IntWritable retweets=new IntWritable(retweetsNum);
            
            if ( splitted[2].matches(trumpMatches[0]) | splitted[2].matches(trumpMatches[1])){
                Text trumpLikes = new Text("trumpLikes");
                Text trumpRetweets = new Text("trumpRetweets");
                context.write(trumpLikes,likes);
                context.write(trumpRetweets,retweets);
            }
            
            if ( splitted[2].matches(bidenMatches[0]) | splitted[2].matches(bidenMatches[1])){
                Text bidenLikes = new Text("bidenLikes");
                Text bidenRetweets = new Text("bidenRetweets");
                context.write(bidenLikes,likes);
                context.write(bidenRetweets,retweets);
            }

        }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(likeAndRetweet.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
