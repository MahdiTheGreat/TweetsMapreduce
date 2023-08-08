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
import java.util.LinkedList;

public class CountryTweetsBySpec {

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
            
            String Lat=splitted[13];
            String Long=splitted[14];
            
            if(!(Lat.equals("") | Long.equals(""))){
            
            float LatFloat=Float.parseFloat(splitted[13]);
            float LongFloat=Float.parseFloat(splitted[14]);
            LinkedList <Text> countryMatches = new LinkedList<Text>();
            
            Text country;
            if(-161.75<LongFloat & LongFloat<-68.0)
            if(19.5<LatFloat & LatFloat<64.85)countryMatches.add(new Text("America"));
            if(-4.65<LongFloat & LongFloat<9.45)
            if(41.6<LatFloat & LatFloat<51.0)countryMatches.add(new Text("France"));
            
            for(int i =0;i<countryMatches.size();i++){
            country=countryMatches.get(i);
            int flag=0;
            
            if ( splitted[2].matches(trumpMatches[0]) | splitted[2].matches(trumpMatches[1])){
                flag+=1;
            }
            
            if ( splitted[2].matches(bidenMatches[0]) | splitted[2].matches(bidenMatches[1])){
                flag+=2;
            }
            if(flag>0){
            IntWritable choice=new IntWritable(flag);
            context.write(country,choice);
            }
            }
  
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
            int trumpTweets=0;
            int bidenTweets=0;
            int bothTweets=0;
            for (IntWritable val : values) {
                int temp = val.get();
                sum+=1;
                if(temp==1){trumpTweets+=1;}
                else if(temp==2){bidenTweets+=1;}
                else if(temp==3){bothTweets+=1;}
                
            }
            
            String reduceResult=key.toString()+" "+Float.toString((float)bothTweets/(float)sum)+" "+Float.toString((float)bidenTweets/(float)sum)+" "+Float.toString((float)trumpTweets/(float)sum);
            
            Text textResult = new Text(reduceResult);
            result.set(sum);
            context.write(textResult, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(CountryTweetsBySpec.class);
        job.setMapperClass(TokenizerMapper.class);
        //job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
