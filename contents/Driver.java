//It took 10,669 milliseconds to crack my 3 letter long password file with one thread. For four threads it took 159 milleseconds. Difference of 10,510 millisecons.
//Took 1675984 milliseconds using 4 threads to crack the 5 letter password. 15786 milliseconds to crack using 3 threads. Difference of 1660198 milliseconds or 27ish minutes.

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.lingala.zip4j.core.*;
import net.lingala.zip4j.exception.*;

/**
 * The Driver class manages the multithreaded password cracking process for a password-protected zip file.
 * It divides the workload among threads and collects results from each thread.
 */
public class Driver {

	public static void main(String[] args) throws InterruptedException {
        String characters = "abcdefghijklmnopqrstuvwxyz";
        int numThreads = 3; // Number of threads
        int length = 5; // Length of the password
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        File zipFile = new File("protected5.zip"); // File to crack

        Manager manager = new Manager();

        long startTime = System.currentTimeMillis();

        // Divide the workload among threads
        int splitWork = characters.length() / numThreads;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
			int start = i * splitWork;
			int end;
		
			if (i == numThreads - 1) {
				end = characters.length(); // Last thread gets the remaining characters
			} else {
				end = start + splitWork; // Other threads get equal sections
			}

            if (end > characters.length()) {
                end = characters.length();
            }
		
			String prefix = characters.substring(start, end);
		
            //Create and start new threads
			threads[i] = new Worker(i, manager, prefix, characters, length, results, zipFile);
			threads[i].start();
		}
		
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Password cracking completed. it took: " + (endTime - startTime) + " milliseconds");
    }
}
