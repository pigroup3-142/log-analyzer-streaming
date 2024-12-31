package services;

import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import models.ApacheAccessLog;
import scala.Tuple2;

public class LogAnalyzerStreaming {
	public static void main(String[] args) throws InterruptedException {		
		SparkConf conf = new SparkConf().setAppName("Log-Analyzer");
		JavaStreamingContext jsc = new JavaStreamingContext(conf, Durations.seconds(10));

		JavaReceiverInputDStream<String> lines = jsc.socketTextStream("localhost", 9999);
		
		// convert input string to ApacheAccessLog object
		JavaDStream<ApacheAccessLog> line = lines.map(new Function<String, ApacheAccessLog>() {
			private static final long serialVersionUID = 1L; // define serial

			@Override
			public ApacheAccessLog call(String log) throws Exception {
				// get 4 parameter need for this math problem
				String[] tmpLine = log.split(" ");
				int lens = tmpLine.length;
				String ipAddress = tmpLine[0]; 
				long contentSize = Long.parseLong(tmpLine[lens-1]);
				int responseCode = Integer.parseInt(tmpLine[lens-2]);
				String endpoint = tmpLine[lens - 4];
				
				return new ApacheAccessLog(ipAddress, "", "", "", "", endpoint, "", responseCode, contentSize);
			}
		}).cache();
		JavaDStream<ApacheAccessLog> windows = line.window(Durations.seconds(30), Durations.seconds(10));
		
		windows.foreachRDD(w -> {
			if(w.count() == 0) {
				System.out.println("No access logs received in this time interval");
			} else {
				// task 1 , same with scala code : 
				// val contentSizes: RDD[Long] = accessLogs.map(_.contentSize).cache()
                // println("Content Size Avg: %s, Min: %s, Max: %s".format(contentSizes.reduce(_+_) / contentSizes.count, contentSizes.min, contentSizes.max))
				JavaRDD<Long> contentSizes = w.map(new Function<ApacheAccessLog, Long>() {
					private static final long serialVersionUID = 2L;

					@Override
					public Long call(ApacheAccessLog apache) throws Exception {
						return apache.getContentSize();
					}
				}).cache();
				
				List<Long> listContentSizes = contentSizes.collect();
				long max_values = 0;
				long min_values = Long.MAX_VALUE;
				
				for(long value : listContentSizes) {
					if(max_values < value) max_values = value;
					if(min_values > value) min_values = value;
				}
				System.out.println("Content size average is " + contentSizes.reduce((a,b) -> (a+b)) / contentSizes.count() 
						+ " Max is " + max_values 
						+ " Min is " + min_values);
				
				// task 2 , same with scala code : 
				// val responseCodeToCount: Array[(Int, Long)] = accessLogs.map(_.responseCode -> 1L).reduceByKey(_+_).take(100)
				// println(s"""Response code counts: ${responseCodeToCount.mkString("[", ",", "]")}""")
				int responseCodeToCount = w.map(new Function<ApacheAccessLog, Integer>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Integer call(ApacheAccessLog apache) throws Exception {
						return 1;
					}
				}).cache().reduce((a,b) -> (a+b));
				System.out.println("Response code count : " + responseCodeToCount);
				
				// task 3 , same with scala code : 
				// val ipAddresses: Array[String] = accessLogs.map(_.ipAddress -> 1L).reduceByKey(_+_).filter(_._2 > 10).map(_._1).take(100)
                // println(s"""IPAddresses > 10 times: ${ipAddresses.mkString("[", ",", "]")}""")
				JavaPairRDD<ApacheAccessLog, Integer> pairs = w.mapToPair(s -> new Tuple2<>(s, 1)).reduceByKey((a,b) -> (a+b)).filter(new Function<Tuple2<ApacheAccessLog,Integer>, Boolean>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Boolean call(Tuple2<ApacheAccessLog, Integer> apache) throws Exception {
						if(apache._2 > 10) {
							return true;
						}
						return false;
					}
				});
				
				System.out.print("IPAddresses > 10 times: ");
				pairs.collect().forEach(p -> {
					System.out.print(p._1.getIpAddress() + " ");
				});
				System.out.println();
			}
		});
		
		jsc.start();             
		jsc.awaitTermination();
	}
}
