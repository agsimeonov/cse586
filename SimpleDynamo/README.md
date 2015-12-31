<h2 class="c3 c19"><span>CSE 486/586 Distributed Systems Programming Assignment 4</span></h2>
<h2 class="c19 c3 c25"><span>Replicated Key-Value Storage</span></h2>
<h3 class="c0"><span>Introduction</span></h3>
<p class="c11 c4 c3"><span>At this point, most of you are probably ready to understand and implement a Dynamo-style key-value storage; this assignment is about implementing a simplified version of Dynamo. (And you might argue that it’s not Dynamo any more ;-) There are three main pieces you need to implement: 1) Partitioning, 2) Replication, and 3) Failure handling.</span></p>
<p class="c11 c4 c3 c17"><span></span></p>
<p class="c11 c4 c3"><span>The main goal is to provide both availability and linearizability at the same time. In other words, your implementation should always perform read and write operations successfully even under failures. At the same time, a read operation should always return the most recent value. To accomplish this goal, this document gives you a guideline of the implementation. </span><span class="c20">However, you have freedom to come up with your own design as long as you provide availability and linearizability at the same time (that is, to the extent that the tester can test)</span><span class="c16">. </span><span>The exception is partitioning and replication, which should be done exactly the way Dynamo does.</span></p>
<p class="c11 c4 c3 c17"><span></span></p>
<p class="c11 c4 c3"><span>This document assumes that you are already familiar with Dynamo. If you are not, that is your first step. There are many similarities between this assignment and the previous assignment for the most basic functionalities, and you are free to reuse your code from the previous assignment.</span></p>
<h3 class="c0"><span>References</span></h3>
<p class="c3"><span>Before we discuss the requirements of this assignment, here are two references for the Dynamo design:</span></p>
<ol class="c2 lst-kix_p8sw3pq6ceee-0 start" start="1">
<li class="c10 c11 c4 c3"><span>Lecture slides on Dynamo: </span><span class="c6"><a class="c8" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring13/lectures/29-dynamo.pptx&amp;sa=D&amp;usg=AFQjCNFdFrIrEWLXlFSw0mx5HLMaYfdFKw">http://www.cse.buffalo.edu/~stevko/courses/cse486/spring13/lectures/29-dynamo.pptx</a></span></li>
<li class="c10 c11 c4 c3"><span>Dynamo paper: </span><span class="c6"><a class="c8" href="https://www.google.com/url?q=http://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf&amp;sa=D&amp;usg=AFQjCNFRD6A_AwABwDZaU78Wyy4KMTp3ww">http://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf</a></span></li>
</ol>
<p class="c11 c4 c3"><span>The lecture slides give an overview, but do not discuss Dynamo in detail, so it should be a good reference to get an overall idea. The paper presents the detail, so it should be a good reference for actual implementation.</span></p>
<h3 class="c0"><a name="h.vjy7zcn4t58b"></a><span>Step 0: Importing the project template</span></h3>
<p class="c3"><span>Just like the previous assignment, we have a project template you can import to Eclipse.</span></p>
<p class="c3 c17"><span></span></p>
<ol class="c2 lst-kix_oi8fd2jrxe3f-0 start" start="1">
<li class="c10 c3"><span>Download</span><span class="c6"><a class="c8" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/SimpleDynamo.zip&amp;sa=D&amp;usg=AFQjCNFTEmi34k9GeSKM7_wbK6WVKV8AUg"> the project template zip file</a></span><span> to a directory.</span></li>
<li class="c10 c3"><span>Import it to your Eclipse workspace.</span></li>
</ol>
<ol class="c2 lst-kix_oi8fd2jrxe3f-1 start" start="1">
<li class="c7 c3"><span>Open Eclipse.</span></li>
<li class="c7 c3"><span>Go to “File” -&gt; “Import”</span></li>
<li class="c7 c3"><span>Select “General/Existing Projects into Workspace” (</span><span class="c20">Caution</span><span>: this is not “Android/Existing Android Code into Workspace”).</span></li>
<li class="c7 c3"><span>In the next screen (which should be “Import Projects”), do the following:</span></li>
</ol>
<ol class="c2 lst-kix_oi8fd2jrxe3f-2 start" start="1">
<li class="c14 c3"><span>Choose “Select archive file:” and select the project template zip file that you downloaded.</span></li>
<li class="c14 c3"><span>Click “Finish.”</span></li>
</ol>
<ol class="c2 lst-kix_oi8fd2jrxe3f-1" start="5">
<li class="c7 c3"><span>At this point, the project template should have been imported to your workspace.</span></li>
</ol>
<ol class="c2 lst-kix_oi8fd2jrxe3f-2 start" start="1">
<li class="c14 c3"><span>You might get an error saying “Android requires compiler compliance level...” If so, right click on “SimpleDynamo” from the Package Explorer, choose “Android Tools” -&gt; “Fix Project Properties” which will fix the error.</span></li>
<li class="c14 c3"><span>You might also get an error about android-support-v4.jar. If so, right click on “SimpleDynamo” from the Package Explorer, choose “Properties” -&gt; “Java Build Path” -&gt; “Libraries” and either fix the android-support-v4.jar’s path or replace it with your SDK’s correct android-support-v4.jar. </span></li>
</ol>
<ol class="c2 lst-kix_oi8fd2jrxe3f-1" start="6">
<li class="c7 c3"><span>Try running it on an AVD and verify that it’s working.</span></li>
</ol>
<ol class="c2 lst-kix_oi8fd2jrxe3f-0" start="3">
<li class="c10 c3"><span>Use the project template for implementing all the components for this assignment.</span></li>
<li class="c10 c3"><span>The template has the package name of “edu.buffalo.cse.cse486586.simpledynamo“. Please do not change this.</span></li>
<li class="c10 c3"><span>The template also defines a content provider authority and class. Please use it to implement your Dynamo functionalities.</span></li>
<li class="c10 c3"><span>We will use SHA-1 as our hash function to generate keys just as last time.</span></li>
<li class="c10 c3"><span>The template is very minimal for this assignment. However, you can reuse any code from your previous submissions.</span></li>
<li class="c10 c3"><span>You can add more to the main Activity in order to test your code. However, this is entirely optional and there is no grading component for your Activity.</span></li>
</ol>
<h3 class="c0"><span>Step 1: Writing the Content Provider</span></h3>
<p class="c11 c4 c3"><span>Just like the previous assignment, the content provider should implement all storage functionalities. For example, it should create server and client threads (if this is what you decide to implement), open sockets, and respond to incoming requests. When writing your system, you can make the following assumptions:</span></p>
<ol class="c2 lst-kix_7pco471774bp-0 start" start="1">
<li class="c10 c11 c4 c3"><span>Just like the previous assignment, you need to support insert/query/delete operations. Also, you need to support @ and * queries.</span></li>
<li class="c10 c11 c4 c3"><span>There are always 5 nodes in the system. There is no need to implement adding/removing nodes from the system.</span></li>
<li class="c10 c11 c4 c3"><span>However, there can be </span><span class="c15">at most 1 node failure at any given time</span><span>. We will emulate a failure only by force closing an app instance. We will </span><span class="c18 c23">not</span><span> emulate a failure by killing an entire emulator instance.</span></li>
<li class="c10 c11 c4 c3"><span class="c15">All failures are temporary;</span><span> you can assume that a failed node will recover soon, i.e., it will not be permanently unavailable during a run.</span></li>
<li class="c10 c11 c4 c3"><span class="c15">When a node recovers, it should copy all the object writes it missed during the failure. </span><span>This can be done by asking the right nodes and copy from them.</span></li>
<li class="c10 c11 c4 c3"><span class="c15">Please focus on correctness rather than performance.</span><span> Once you handle failures correctly, if you still have time, you can improve your performance.</span></li>
<li class="c10 c11 c4 c3"><span>Your content provider should support </span><span class="c15">concurrent read/write operations</span><span>.</span></li>
<li class="c10 c11 c4 c3"><span>Your content provider should handle </span><span class="c15">a failure happening at the same time with read/write operations</span><span>.</span></li>
<li class="c10 c4 c3 c11"><span class="c15">Replication should be done exactly the same way as Dynamo does.</span><span> In other words, a (key, value) pair should be replicated over three consecutive partitions, starting from the partition that the key belongs to.</span></li>
<li class="c10 c11 c4 c3"><span class="c16">Unlike Dynamo, there are two things you do not need to implement.</span></li>
</ol>
<ol class="c2 lst-kix_7pco471774bp-1 start" start="1">
<li class="c1"><span>Virtual nodes: Your implementation should use physical nodes rather than virtual nodes, i.e., all partitions are static and fixed.</span></li>
<li class="c1"><span>Hinted handoff: Your implementation do not need to implement hinted handoff. This means that when there is a failure, it is OK to replicate on only two nodes.</span></li>
</ol>
<ol class="c2 lst-kix_7pco471774bp-0" start="11">
<li class="c10 c11 c4 c3"><span>All replicas should store the same value for each key. This is “per-key” consistency. There is no consistency guarantee you need to provide across keys. More formally, you need to implement </span><span class="c15">per-key linearizability</span><span>.</span></li>
<li class="c10 c11 c4 c3"><span>Each content provider instance should have a node id derived from its emulator port. </span><span>This node id should be obtained by applying the above hash function (i.e., genHash()) to the emulator port.</span><span> For example, the node id of the content provider instance running on emulator-5554 should be, </span><span class="c23">node_id = genHash(“5554”)</span><span>. This is necessary to find the correct position of each node in the Dynamo ring.</span></li>
<li class="c10 c11 c4 c3"><span>Your content provider’s URI should be </span><span>“content://edu.buffalo.cse.cse486586.simpledynamo.provider”, which means that any app should be able to access your content provider using that URI. This is already defined in the template, so please don’t change this. Your content provider does not need to match/support any other URI pattern.</span></li>
<li class="c10 c3"><span>We have fixed the ports &amp; sockets.</span></li>
</ol>
<ol class="c2 lst-kix_7pco471774bp-1 start" start="1">
<li class="c7 c3"><span>Your app should open one server socket that listens on 10000.</span></li>
<li class="c7 c3"><span>You need to use run_avd.py and set_redir.py to set up the testing environment.</span></li>
<li class="c7 c3"><span>The grading will use 5 AVDs. The redirection ports are 11108, 11112, 11116, 11120, and 11124.</span></li>
<li class="c7 c3"><span>You should just hard-code the above 5 ports and use them to set up connections.</span></li>
<li class="c7 c3"><span>Please use the code snippet provided in PA1 on how to determine your local AVD.</span></li>
</ol>
<ol class="c2 lst-kix_7pco471774bp-2 start" start="1">
<li class="c14 c3"><span>emulator-5554: “5554”</span></li>
<li class="c14 c3"><span>emulator-5556: “5556”</span></li>
<li class="c3 c14"><span>emulator-5558: “5558”</span></li>
<li class="c14 c3"><span>emulator-5560: “5560”</span></li>
<li class="c14 c3"><span>emulator-5562: “5562”</span></li>
</ol>
<ol class="c2 lst-kix_7pco471774bp-0" start="15">
<li class="c10 c3"><span>Any app (not just your app) should be able to access (read and write) your content provider. As with the previous assignment, please do not include any permission to access your content provider.</span></li>
</ol>
<p class="c11 c4 c3 c17"><span></span></p>
<p class="c11 c4 c3"><span>The following is a guideline for your content provider based on the design of Amazon Dynamo:</span></p>
<ol class="c2 lst-kix_adk1ysr35hnq-0 start" start="1">
<li class="c10 c3"><span class="c9">Membership</span></li>
</ol>
<ol class="c2 lst-kix_adk1ysr35hnq-1 start" start="1">
<li class="c7 c3"><span class="c15">Just as the original Dynamo, every node can know every other node.</span><span> This means that each node knows all other nodes in the system and also knows exactly which partition belongs to which node; any node can forward a request to the correct node without using a ring-based routing.</span></li>
</ol>
<ol class="c2 lst-kix_adk1ysr35hnq-0" start="2">
<li class="c10 c3"><span class="c9">Request routing</span></li>
</ol>
<ol class="c2 lst-kix_adk1ysr35hnq-1 start" start="1">
<li class="c7 c3"><span>Unlike Chord, each Dynamo node knows all other nodes in the system and also knows exactly which partition belongs to which node.</span></li>
<li class="c1"><span>Under no failures, all requests are directly forwarded to the coordinator, and the coordinator should be in charge of serving read/write operations.</span></li>
</ol>
<ol class="c2 lst-kix_adk1ysr35hnq-0" start="3">
<li class="c10 c3"><span class="c9">Quorum replication</span></li>
</ol>
<ol class="c2 lst-kix_adk1ysr35hnq-1 start" start="1">
<li class="c1"><span>For linearizability, you can implement a quorum-based replication used by Dynamo.</span></li>
<li class="c1"><span>Note that the original design does not provide linearizability. You need to adapt the design.</span></li>
<li class="c1"><span class="c15">The replication degree N should be 3.</span><span> This means that given a key, the key’s coordinator as well as the 2 successor nodes in the Dynamo ring should store the key.</span></li>
<li class="c1"><span class="c15">Both the reader quorum size R and the writer quorum size W should be 2.</span></li>
<li class="c1"><span>T</span><span>he coordinator for a get/put request should </span><span class="c5">always contact other two nodes</span><span> and get the votes.</span></li>
<li class="c1"><span>For write operations, all objects can be </span><span class="c20">versioned</span><span> in order to distinguish stale copies from the most recent copy.</span></li>
<li class="c1"><span>For read operations, if the readers in the reader quorum have different versions of the same object, the coordinator should pick the most recent version and return it.</span></li>
</ol>
<ol class="c2 lst-kix_adk1ysr35hnq-0" start="4">
<li class="c10 c11 c4 c3"><span class="c9">Chain replication</span></li>
</ol>
<ol class="c2 lst-kix_adk1ysr35hnq-1 start" start="1">
<li class="c1"><span>Another replication strategy you can implement is chain replication, which provides linearizability.</span></li>
<li class="c1"><span>If you are interested in more details, please take a look at the following paper: </span><span class="c6"><a class="c8" href="https://www.google.com/url?q=http://www.cs.cornell.edu/home/rvr/papers/osdi04.pdf&amp;sa=D&amp;usg=AFQjCNEq0Wt3tFdRcXgCI7Ny5DstuhLfXw">http://www.cs.cornell.edu/home/rvr/papers/osdi04.pdf</a></span></li>
<li class="c1"><span>In chain replication, a write operation always comes to the first partition; then it propagates to the next two partitions in sequence. The last partition returns the result of the write.</span></li>
<li class="c1"><span>A read operation always comes to the last partition and reads the value from the last partition.</span></li>
</ol>
<ol class="c2 lst-kix_adk1ysr35hnq-0" start="5">
<li class="c10 c11 c4 c3"><span class="c9">Failure handling</span></li>
</ol>
<ol class="c2 lst-kix_adk1ysr35hnq-1 start" start="1">
<li class="c1"><span>Handling failures should be done very carefully because there can be many corner cases to consider and cover.</span></li>
<li class="c1"><span>Just as the original Dynamo, each request can be used to detect a node failure.</span></li>
<li class="c1"><span class="c15">For this purpose, you can use a timeout for a socket read;</span><span> you can pick a reasonable timeout value, e.g., 100 ms, and if a node does not respond within the timeout, you can consider it a failure.</span></li>
<li class="c1"><span class="c18 c23">Do not rely on socket creation or connect status to determine if a node has failed.</span><span> Due to the Android emulator networking setup, it is </span><span class="c18">not</span><span> safe to rely on socket creation or connect status to judge node failures. Please use an explicit method to test whether an app instance is running or not, e.g., using a socket read timeout as described above.</span></li>
<li class="c1"><span>When a coordinator for a request fails and it does not respond to the request, </span><span class="c15">its successor can be contacted next for the request.</span></li>
</ol>
<h4 class="c3 c12"><a name="h.j93vpbbyu58w"></a><span class="c22 c27">Testing</span></h4>
<p class="c4 c3"><span>We have testing programs to help you see how your code does with our grading criteria. There are 6 phases in testing.</span></p>
<ol class="c2 lst-kix_xbo6yej6zb69-0 start" start="1">
<li class="c10 c4 c3"><span>Testing basic ops</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-1 start" start="1">
<li class="c7 c4 c3"><span>This phase will test basic operations, i.e., insert, query, delete, @, and *. This will test if everything is correctly replicated. There is no concurrency in operations and there is no failure either.</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-0" start="2">
<li class="c10 c4 c3"><span>Testing concurrent ops with different keys</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-1 start" start="1">
<li class="c7 c4 c3"><span>This phase will test if your implementation can handle concurrent operations under no failure.</span></li>
<li class="c7 c4 c3"><span>The tester will use independent (key, value) pairs inserted/queried concurrently on all the nodes.</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-0" start="3">
<li class="c10 c4 c3"><span>Testing concurrent ops with same keys</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-1 start" start="1">
<li class="c7 c4 c3"><span>This phase will test if your implementation can handle concurrent operations with same keys under no failure.</span></li>
<li class="c7 c4 c3"><span>The tester will use the same set of (key, value) pairs inserted/queried concurrently on all the nodes.</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-0" start="4">
<li class="c10 c4 c3"><span>Testing one failure</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-1 start" start="1">
<li class="c7 c4 c3"><span>This phase will test one failure with every operation.</span></li>
<li class="c4 c3 c7"><span>One node will crash before operations start. After all the operations are done, the node will recover.</span></li>
<li class="c7 c4 c3"><span>This will be repeated for each and every operation.</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-0" start="5">
<li class="c10 c4 c3"><span>Testing concurrent operations with one failure</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-1 start" start="1">
<li class="c7 c4 c3"><span>This phase will execute operations concurrently and crash one node in the middle of the execution. After some time, the failed node will also recover in the middle of the execution.</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-0" start="6">
<li class="c10 c4 c3"><span>Testing concurrent operations with one consistent failure</span></li>
</ol>
<ol class="c2 lst-kix_xbo6yej6zb69-1 start" start="1">
<li class="c7 c4 c3"><span>This phase will crash one node at a time consistently, i.e., one node will crash then recover, and another node will crash and recover, etc.</span></li>
<li class="c7 c4 c3"><span>There will be a brief period of time in between the crash-recover sequence.</span></li>
</ol>
<p class="c4 c3 c17"><span></span></p>
<p class="c3 c4"><span>Each testing phase is quite intensive (i.e., it will take some time for each phase to finish), so the tester allows you to specify which testing phase you want to test. You won’t have to wait until everything is finished every time. However, you still need to make sure that you run the tester in its entirety before you submit. </span><span class="c5">We will not test individual testing phases separately in our grading.</span></p>
<ul class="c2 lst-kix_wimzmg920o99-0 start">
<li class="c10 c4 c3"><span>You can specify which testing phase you want to test by providing ‘-p’ or ‘--phase’ argument to the tester.</span></li>
<li class="c10 c4 c3"><span>‘-h’ argument will show you what options are available.</span></li>
<li class="c10 c3"><span>Download a testing program for your platform. If your platform does not run it, please report it on Piazza.</span></li>
</ul>
<ul class="c2 lst-kix_wimzmg920o99-1 start">
<li class="c7 c3"><span class="c6"><a class="c8" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/simpledynamo-grading.exe&amp;sa=D&amp;usg=AFQjCNF-DPWmmmVtrhz8GHkcN0Y5skXv-Q">Windows</a></span><span>: We’ve tested it on 32- and 64-bit Windows 8.</span></li>
<li class="c7 c3"><span class="c6"><a class="c8" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/simpledynamo-grading.linux&amp;sa=D&amp;usg=AFQjCNHO3X9qwMQvfn3DWYRMcwtgvLFNew">Linux</a></span><span>: We’ve tested it on 32- and 64-bit Ubuntu 12.04.</span></li>
<li class="c7 c3"><span class="c6"><a class="c8" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/simpledynamo-grading.osx&amp;sa=D&amp;usg=AFQjCNHU7-mGxc9wKum7f5BouqIdxqbncw">OS X</a></span><span>: We’ve tested it on 32- and 64-bit OS X 10.9 Mavericks.</span></li>
</ul>
<ul class="c2 lst-kix_wimzmg920o99-0">
<li class="c10 c3"><span>Before you run the program, please make sure that you are running five AVDs. </span><span class="c13">python run_avd.py 5</span><span> will do it.</span></li>
<li class="c10 c3"><span>Run the testing program from the command line.</span></li>
<li class="c3 c10"><span>On your terminal, it will give you your partial and final score, and in some cases, problems that the testing program finds.</span></li>
</ul>
<h3 class="c0"><span>Submission</span></h3>
<p class="c3"><span>We use the CSE submit script. You need to use either “</span><span class="c24">submit_cse486” or “submit_cse586”, depending on your registration status.</span><span> If you haven’t used it, the instructions on how to use it is here:</span><span><a class="c8" href="https://www.google.com/url?q=https://wiki.cse.buffalo.edu/services/content/submit-script&amp;sa=D&amp;usg=AFQjCNFyjQ53f5E_2Dq-aJeROWHah33f-w"> </a></span><span class="c21"><a class="c8" href="https://www.google.com/url?q=https://wiki.cse.buffalo.edu/services/content/submit-script&amp;sa=D&amp;usg=AFQjCNFyjQ53f5E_2Dq-aJeROWHah33f-w">https://wiki.cse.buffalo.edu/services/content/submit-script</a></span></p>
<p class="c3 c17"><span class="c21"><a class="c8" href="https://www.google.com/url?q=https://wiki.cse.buffalo.edu/services/content/submit-script&amp;sa=D&amp;usg=AFQjCNFyjQ53f5E_2Dq-aJeROWHah33f-w"></a></span></p>
<p class="c3"><span>You need to submit one file described below. </span><span class="c20">Once again, you must follow everything below exactly. Otherwise, you will get no point on this assignment.</span></p>
<ul class="c2 lst-kix_atshss1d2koa-0 start">
<li class="c10 c3"><span>Your entire Eclipse project source code tree zipped up in .zip: The name should be SimpleDynamo.zip. </span><span class="c20">Please do not change the name.</span><span> To do this, please do the following</span></li>
</ul>
<ol class="c2 lst-kix_atshss1d2koa-1 start" start="1">
<li class="c7 c3"><span>Open Eclipse.</span></li>
<li class="c7 c3"><span>Go to “File” -&gt; “Export”.</span></li>
<li class="c7 c3"><span>Select “General -&gt; Archive File”.</span></li>
<li class="c7 c3"><span>Select your project. Make sure that you include all the files and check “Save in zip format”.</span></li>
<li class="c7 c3"><span class="c20">Please do not use any other compression tool other than zip, i.e., no 7-Zip, no RAR, etc.</span></li>
</ol>
<h3 class="c0"><span>Deadline: </span><span class="c20">5/9/14 (Friday) 1:59pm</span></h3>
<p class="c3"><span>The deadline is firm; if your timestamp is 2pm, it is a late submission.</span></p>
<h3 class="c0"><span>Grading</span></h3>
<p class="c3"><span>This assignment is 15% of your final grade. Also there is extra credit if you pass all 6 phases.</span></p>
<ul class="c2 lst-kix_slk72wadkuy5-0 start">
<li class="c10 c3"><span>Phase 1: 2%</span></li>
<li class="c10 c3"><span>Phase 2: 3%</span></li>
<li class="c10 c3"><span>Phase 3: 2%</span></li>
<li class="c10 c3"><span>Phase 4: 4%</span></li>
<li class="c10 c3"><span>Phase 5: 4%</span></li>
<li class="c10 c3"><span>Phase 6: 3%</span></li>
</ul>
</div>
