# TweetsMapreduce
In this project, using the Yarn framework, we set up a Hadoop cluster using three virtual machines and run some MapReduce programs on it. To create virtual machines, install Hadoop, and set up the cluster, the steps mentioned in the link below are used:

https://pnunofrancog.medium.com/how-to-set-up-hadoop-3-2-1-multi-node-cluster-on-ubuntu-20-04-inclusive-terminology-2dc17b1bff19

In this project, we use Hadoop-3.2.2 and also set replication to one (since we are just doing this project mainly as a demonstration). 

To create the cluster, We assign 1vCPU, 1 GB RAM, and 20 GB of disk memory to the first virtual machine, and 2vCPU and more memory (for example, 2 GB) to the second and third virtual machines. If the steps have been followed correctly, the installation is done in such a way that the first virtual machine assumes the roles of NameNode and ResourceManager and the second and third virtual machines assume the roles of DataNode and NodeManager. Using the jps command we can see if we have done things correctly, as can be seen below:

![image](https://github.com/MahdiTheGreat/TweetsMapreduce/assets/47212121/5964a19c-b3a1-43f0-8934-b11e3544788b)

![image](https://github.com/MahdiTheGreat/TweetsMapreduce/assets/47212121/539a5579-7d40-428c-8de1-a6454aa2123c)

![image](https://github.com/MahdiTheGreat/TweetsMapreduce/assets/47212121/71efbdbf-a093-4f67-8e54-1b57f852acdc)

The ‎WebGUI‏ of the cluster can also be accessed from our computer:

![image](https://github.com/MahdiTheGreat/TweetsMapreduce/assets/47212121/225b1fa7-03e3-4c10-9c10-defec98313bb)

![image](https://github.com/MahdiTheGreat/TweetsMapreduce/assets/47212121/5cd1c4de-98a9-464f-9bf2-387d5617580e)

From the active nodes section in WebGUI, we can get some information about the nodes' resources and examine the correlation of this information with the resources we have assigned to the virtual machines. As can be seen in the images below, the accessible external memory is equal to 19.56 GB, which is close to the memory allocated to the primary VM (with NameNode), which is equal to 20 GB, of which 4.31 GB is allocated to DFS. It can also be seen that the number of live nodes is equal to two, and these nodes are h-secondary1 and h-secondary2, which have the data nodes, and each has access to 9.78 GB (total equal to 19.56 ) of external memory.

![image](https://github.com/MahdiTheGreat/TweetsMapreduce/assets/47212121/8a1a910f-bbcf-4c47-8c3c-d9bfe1560dfa)

![image](https://github.com/MahdiTheGreat/TweetsMapreduce/assets/47212121/498e47fe-e6ce-40df-8c7a-8e116b2e7095)

# Dataset Description 
This dataset contains 172 million tweets about the US election. The records of this dataset have 21 columns. You can see the available information about the columns of this dataset in the link below.

https://www.kaggle.com/manchunhui/us-election-2020-tweets?select=hashtag_joebiden.csv

However, to reduce the project load, we use a different database, which is in datasets.zip. Below is some more additional information about the datasets:

- The tweets in new_hashtag_donaldtrump.csv have the hashtags #DonaldTrump or #Trump and the tweets in new_hashtag_joebiden.csv have the hashtags #JoeBiden and #Biden. Note that there may be, for example, tweets in the file new_hashtag_donaldtrump.csv that also contain the #Biden hashtag.
-  ‎In some records of the datasete, a field's value may be empty or null

# Development and implementation of Mapreduce programs

Using HDFS CLI, we create /user/hadoop folder in HDFS. Next, we unzip the datasets.zip file and extract the two CSV files in the /datasets/US_election path using the HDFS CLI in HDFS, for example in the path Load /user/hadoop/input with 1 replication. Pay attention that both files will be checked at the same time by running the program only once.

We write a MapReduce program in likeAndRetweet.java that counts the total number of likes and the total number of retweets for tweets related to both candidates Joe Biden and Donald Trump. The output can be seen below:

bidenLikes	9987329
bidenRetweets	2086461
trumpLikes	9349624
trumpRetweets	2078414

Next we write a MapReduce program in CountryTweets.java that shows what percentage of tweets in each of the following countries are about both candidates, Joe Biden and Donald Trump respectively:

- America
- Iran
- Netherlands
- Austria
- Mexico
- Emirates
- France
- Germany
- Canada
- Spain
- Italy

For this, we use the country field. The country field in the dataset does not necessarily contain standard values; Therefore, to write this program, it is necessary to check that each of the names of the above countries are present in the COUNTRY field. For example, if the value of this field for a tweet was equal to "somewhere in Iran", we consider this tweet to be in Iran. We also perform this search in a case-insensitive manner. Finally, the total number of tweets related to that country is also mentioned. The output can be seen below:

<code>America 0.18529914 0.36928996 0.4454109	332484
Austria 0.33100882 0.18921432 0.47977686	2151
Canada 0.20643142 0.2853135 0.5082551	27801
Emirates 0.40140188 0.29485983 0.30373833	2140
France 0.4142161 0.20681058 0.3789733	35298
Germany 0.32195964 0.19972296 0.4783174	35374
Iran 0.30497476 0.222062 0.47296324	1387
Italy 0.34374377 0.2708209 0.38543534	20076
Mexico 0.40878657 0.26304686 0.32816657	10903
Netherlands 0.31765893 0.2559704 0.42637068	14865
Spain 0.37975731 0.2186983 0.4015444	7252
United Kingdom 0.24617557 0.27885544 0.474969	58048</code>

For example in the output for America, 18% of tweets were about both candidates, 36% were about Joe Biden and 44% of were about Donald Trump. Also, 332484 of all the tweets in the datasets belonged to America.

Finally, we write A MapReduce program in CountryTweetsBySpec.java with the same functionality and output format as CountryTweets.java with the difference that this time we use latitude and longitude to determine the country from which the tweet was sent. In this program, we only examine the tweets related to the countries of America and France. The longitude and latitude of the countries of America and France are approximate as follows:

- America
<br><code>-68 > Longitude > -161.75
64.85 > Latitude > 19.5</code></br>
- France
  
<code>9.45 > Longitude > -4.65
51 > Latitude > 41.6</code>

The output can be seen below:

<code>America 0.1932835 0.3576839 0.4490326	425370
France 0.38364026 0.21766117 0.39869857	53326</code>

As can be seen, the percentages related to America and France are quite close to each other in the two answers, and the biggest difference between them is about three percent, but the number of tweets in the two answers is very different. This indicates that the parameters of longitude and latitude are not appropriate for data classification.




