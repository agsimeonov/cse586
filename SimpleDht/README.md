<h2 class="c9 c21"><span>CSE 486/586 Distributed Systems Programming Assignment 3</span></h2>
<h2 class="c21 c9 c29"><span>Simple DHT</span></h2>
<h3 class="c14 c9"><span>Introduction</span></h3>
<p class="c7"><span>In this assignment, you will design a simple DHT based on Chord. Although the design is based on Chord, it is a simplified version of Chord; you do not need to implement finger tables and finger-based routing; you also do not need to handle node leaves/failures.Therefore, there are three things you need to implement: 1) ID space partitioning/re-partitioning, 2) Ring-based routing, and 3) Node joins.</span></p>
<p class="c7 c10"><span></span></p>
<p class="c7"><span>Just like the previous assignment, your app should have an activity and a content provider. However, the main activity should be used for testing only and should not implement any DHT functionality. The content provider should implement all DHT functionalities and support insert and query operations. Thus, if you run multiple instances of your app, all content provider instances should form a Chord ring and serve insert/query requests in a distributed fashion according to the Chord protocol.</span></p>
<h3 class="c14 c9"><span>References</span></h3>
<p class="c9"><span>Before we discuss the requirements of this assignment, here are two references for the Chord design:</span></p>
<ol class="c3 lst-kix_p8sw3pq6ceee-0 start" start="1">
<li class="c4"><span>Lecture slides on Chord: </span><span class="c15"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/lectures/13-dht.pptx&amp;sa=D&amp;usg=AFQjCNH99PlGOaqFyPwnnfZNGf07E9zxHg">http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/lectures/13-dht.pptx</a></span></li>
<li class="c7 c5 c8"><span>Chord paper: </span><span class="c15"><a class="c6" href="https://www.google.com/url?q=http://conferences.sigcomm.org/sigcomm/2001/p12-stoica.pdf&amp;sa=D&amp;usg=AFQjCNGpFCR094mTiTDp4RJn0zOGdGAyIQ">http://conferences.sigcomm.org/sigcomm/2001/p12-stoica.pdf</a></span></li>
</ol>
<p class="c7"><span>The lecture slides give an overview, but do not discuss Chord in detail, so it should be a good reference to get an overall idea. The paper presents pseudo code for implementing Chord, so it should be a good reference for actual implementation.</span></p>
<h3 class="c9"><a name="h.rgc9caxb3eea"></a><span>Note</span></h3>
<p class="c9"><span class="c1">It is important to remember that this assignment does not require you to implement everything about Chord. </span><span>Mainly, there are three things you </span><span class="c1 c19">do not</span><span class="c19"> </span><span>need to consider from the Chord paper.</span></p>
<ol class="c3 lst-kix_p79jy2a6pjy5-0 start" start="1">
<li class="c7 c5 c8"><span>Fingers and finger-based routing (i.e., Section 4.3 &amp; any discussion about fingers in Section 4.4)</span></li>
<li class="c7 c5 c8"><span>Concurrent node joins (i.e., Section 5)</span></li>
<li class="c7 c5 c8"><span>Node leaves/failures (i.e., Section 5)</span></li>
</ol>
<p class="c7"><span>We will discuss this more</span><span> in “Step 2: Writing a Content Provider” below.</span></p>
<h3 class="c14 c9"><a name="h.itf53a7o9rus"></a><span>Step 0: Importing the project template</span></h3>
<p class="c9"><span>Just like the previous assignment, we have a project template you can import to Eclipse.</span></p>
<ol class="c3 lst-kix_fuidbk3zsne2-0 start" start="1">
<li class="c4"><span>Download</span><span class="c15"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/SimpleDht.zip&amp;sa=D&amp;usg=AFQjCNEm8GTQEE1yNmX4LH-1XRghCgjFUA"> the project template zip file</a></span><span> to a directory.</span></li>
<li class="c4"><span>Import it to your Eclipse workspace.</span></li>
</ol>
<ol class="c3 lst-kix_fuidbk3zsne2-1 start" start="1">
<li class="c2"><span>Open Eclipse.</span></li>
<li class="c2"><span>Go to “File” -&gt; “Import”</span></li>
<li class="c2"><span>Select “General/Existing Projects into Workspace” (</span><span class="c22">Caution</span><span>: this is not “Android/Existing Android Code into Workspace”).</span></li>
<li class="c2"><span>In the next screen (which should be “Import Projects”), do the following:</span></li>
</ol>
<ol class="c3 lst-kix_fuidbk3zsne2-2 start" start="1">
<li class="c5 c16 c9"><span>Choose “Select archive file:” and select the project template zip file that you downloaded.</span></li>
<li class="c5 c16 c9"><span>Click “Finish.”</span></li>
</ol>
<ol class="c3 lst-kix_fuidbk3zsne2-1" start="5">
<li class="c2"><span>At this point, the project template should have been imported to your workspace.</span></li>
</ol>
<ol class="c3 lst-kix_fuidbk3zsne2-2 start" start="1">
<li class="c5 c16 c9"><span>You might get an error saying “Android requires compiler compliance level...” If so, right click on “SimpleDht” from the Package Explorer, choose “Android Tools” -&gt; “Fix Project Properties” which will fix the error.</span></li>
<li class="c5 c16 c9"><span>You might also get an error about android-support-v4.jar. If so, right click on “SimpleDht” from the Package Explorer, choose “Properties” -&gt; “Java Build Path” -&gt; “Libraries” and either fix the android-support-v4.jar’s path or replace it with your SDK’s correct android-support-v4.jar.</span></li>
</ol>
<ol class="c3 lst-kix_fuidbk3zsne2-1" start="6">
<li class="c2"><span>Try running it on an AVD and verify that it’s working.</span></li>
</ol>
<ol class="c3 lst-kix_fuidbk3zsne2-0" start="3">
<li class="c4"><span>Use the project template for implementing all the components for this assignment.</span></li>
<li class="c4"><span>The template has the package name of “edu.buffalo.cse.cse486586.simpledht“. Please do not change this.</span></li>
<li class="c4"><span>The template also defines a content provider authority and class. Please use it to implement your Chord functionalities.</span></li>
<li class="c4"><span>We will use SHA-1 as our hash function to generate keys. The following code snippet takes a string and generates a SHA-1 hash as a hexadecimal string. Please use it to generate your keys. The template already has the code, so you just need to use it at appropriate places. Given two keys, you can use the standard lexicographical string comparison to determine which one is greater.</span></li>
</ol>
<p class="c7 c10"><span></span></p>
<a href="#" name="85685ae4e1acade45a0c30e835f534eec9e4af31"></a><a href="#" name="0"></a>
<table cellpadding="0" cellspacing="0" class="c28">
<tbody>
<tr class="c12">
<td class="c24" colspan="1" rowspan="1">
<p class="c7"><span class="c13">import java.security.MessageDigest;</span></p>
<p class="c7"><span class="c13">import java.security.NoSuchAlgorithmException;</span></p>
<p class="c7"><span class="c13">import java.util.Formatter;</span></p>
<p class="c7 c10"><span class="c13"></span></p>
<p class="c7"><span class="c13">private String genHash(String input) throws NoSuchAlgorithmException {</span></p>
<p class="c7 c8"><span class="c13">MessageDigest sha1 = MessageDigest.getInstance(&quot;SHA-1&quot;);</span></p>
<p class="c7 c8"><span class="c13">byte[] sha1Hash = sha1.digest(input.getBytes());</span></p>
<p class="c7 c8"><span class="c13">Formatter formatter = new Formatter();</span></p>
<p class="c7 c8"><span class="c13">for (byte b : sha1Hash) {</span></p>
<p class="c0"><span class="c13">formatter.format(&quot;%02x&quot;, b);</span></p>
<p class="c7 c8"><span class="c13">}</span></p>
<p class="c7 c8"><span class="c13">return formatter.toString();</span></p>
<p class="c7"><span class="c13">}</span></p>
</td>
</tr>
</tbody>
</table>
<h3 class="c14 c9"><span>Step 1: Writing the Content Provider</span></h3>
<p class="c7"><span>First of all, your app should have a content provider. This content provider should implement all DHT functionalities. For example, it should create server and client threads (if this is what you decide to implement), open sockets, and respond to incoming requests; it should also implement a simplified version of the Chord routing protocol; lastly, it should also handle node joins. The following are the requirements for your content provider:</span></p>
<ol class="c3 lst-kix_adk1ysr35hnq-0 start" start="1">
<li class="c4"><span>We will test your app with any number of instances up to 5 instances.</span></li>
<li class="c4"><span>The content provider should implement all DHT functionalities. This includes all communication as well as mechanisms to handle insert/query requests and node joins.</span></li>
<li class="c4"><span>Each content provider instance should have a node id derived from its emulator port. </span><span class="c1">This node id should be obtained by applying the above hash function (i.e., genHash()) to the emulator port.</span><span> For example, the node id of the content provider instance running on emulator-5554 should be, </span><span class="c11">node_id = genHash(“5554”)</span><span>. This is necessary to find the correct position of each node in the Chord ring.</span></li>
<li class="c7 c5 c8"><span>Your content provider should implement insert(), query(), and delete(). The basic interface definition is the same as the previous assignment, which allows a client app to insert arbitrary &lt;”key”, “value”&gt; pairs where both the key and the value are strings.</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-1 start" start="1">
<li class="c0 c5"><span>For delete(URI uri, String selection, String[] selectionArgs), you only need to use use the first two parameters, uri &amp; selection.  This is similar to what you need to do with query().</span></li>
<li class="c0 c5"><span class="c1">However, please keep in mind that this “key” should be hashed by the above genHash() before getting inserted to your DHT in order to find the correct position in the Chord ring.</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-0" start="5">
<li class="c7 c5 c8"><span>For your query() and delete(), you need to recognize two special strings for the </span><span class="c11">selection</span><span> parameter.</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-1 start" start="1">
<li class="c0 c5"><span>If “*” (a string with a single character *) is given as the </span><span class="c11">selection</span><span> parameter to query(), then you need to return all &lt;key, value&gt; pairs stored in your entire DHT.</span></li>
<li class="c0 c5"><span>Similarly, if “*” is given as the </span><span class="c11">selection</span><span> parameter to delete(), then you need to delete all &lt;key, value&gt; pairs stored in your entire DHT.</span></li>
<li class="c0 c5"><span>If “@” (a string with a single character @) is given as the </span><span class="c11">selection</span><span> parameter to query() on an AVD, then you need to return all &lt;key, value&gt; pairs </span><span class="c1">stored in your local partition of the node</span><span>, i.e., all &lt;key, value&gt; pairs stored locally in the AVD on which you run query().</span></li>
<li class="c0 c5"><span>Similarly, if “@” is given as the </span><span class="c11">selection</span><span> parameter to delete() on an AVD, then you need to delete all &lt;key, value&gt; pairs </span><span class="c1">stored in your local partition of the node</span><span>, i.e., all &lt;key, value&gt; pairs stored locally in the AVD on which you run delete().</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-0" start="6">
<li class="c7 c5 c8"><span>An app that uses your content provider can give arbitrary &lt;key, value&gt; pairs, e.g., &lt;”I want to”, “store this”&gt;; then your content provider should hash the key via genHash(), e.g., genHash(“I want to”), get the correct position in the Chord ring based on the hash value, and store &lt;”I want to”, “store this”&gt; in the appropriate node.</span></li>
<li class="c4"><span>Your content provider should implement ring-based routing. Following the design of Chord, your content provider should maintain predecessor and successor pointers and forward each request to its successor until the request arrives at the correct node. Once the correct node receives the request, it should process it and return the result (directly or recursively) to the original content provider instance that first received the request.</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-1 start" start="1">
<li class="c2"><span>Your content provider do not need to maintain finger tables and implement finger-based routing. This is not required.</span></li>
<li class="c2"><span>As with the previous assignment, we will fix all the port numbers (see below). This means that you can use the port numbers (11108, 11112, 11116, 11120, &amp; 11124) as your successor and predecessor pointers.</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-0" start="8">
<li class="c4"><span>Your content provider should handle new node joins. </span><span class="c22">For this, you need to have the first emulator instance (i.e., emulator-5554) receive all new node join requests.</span><span> Your implementation should not choose a random node to do that. Upon completing a new node join request, affected nodes should have updated their predecessor and successor pointers correctly.</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-1 start" start="1">
<li class="c2"><span>Your content provider do not need to handle concurrent node joins. You can assume that a node join will only happen once the system completely processes the previous join.</span></li>
<li class="c2"><span>Your content provider do not need to handle insert/query requests while a node is joining. You can assume that insert/query requests will be issued only with a stable system.</span></li>
<li class="c0 c5"><span>Your content provider do not need to handle node leaves/failures. This is not required.</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-0" start="9">
<li class="c7 c5 c8"><span>We have fixed the ports &amp; sockets.</span></li>
</ol>
<ol class="c3 lst-kix_79ov0o9xrcwj-1 start" start="1">
<li class="c2"><span>Your app should open one server socket that listens on 10000.</span></li>
<li class="c2"><span>You need to use run_avd.py and set_redir.py to set up the testing environment.</span></li>
<li class="c2"><span>The grading will use 5 AVDs. The redirection ports are 11108, 11112, 11116, 11120, and 11124.</span></li>
<li class="c2"><span>You should just hard-code the above 5 ports and use them to set up connections.</span></li>
<li class="c2"><span>Please use the code snippet provided in PA1 on how to determine your local AVD.</span></li>
</ol>
<ol class="c3 lst-kix_79ov0o9xrcwj-2 start" start="1">
<li class="c5 c16 c9"><span>emulator-5554: “5554”</span></li>
<li class="c5 c9 c16"><span>emulator-5556: “5556”</span></li>
<li class="c5 c16 c9"><span>emulator-5558: “5558”</span></li>
<li class="c5 c16 c9"><span>emulator-5560: “5560”</span></li>
<li class="c5 c16 c9"><span>emulator-5562: “5562”</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-0" start="10">
<li class="c4"><span>Your content provider’s URI should be: </span><span>“content://edu.buffalo.cse.cse486586.simpledht.provider”, which means that any app should be able to access your content provider using that URI. Your content provider does not need to match/support any other URI pattern.</span></li>
<li class="c4"><span>As with the previous assignment, Your provider should have two columns.</span></li>
</ol>
<ol class="c3 lst-kix_77p2mdz25lu2-1 start" start="1">
<li class="c2"><span>The first column should be named as “key” (an all lowercase string without the quotation marks). This column is used to store all keys.</span></li>
<li class="c2"><span>The second column should be named as “value” (an all lowercase string without the quotation marks). This column is used to store all values.</span></li>
<li class="c2"><span>All keys and values that your provider stores should use the string data type.</span></li>
</ol>
<ol class="c3 lst-kix_adk1ysr35hnq-0" start="12">
<li class="c7 c5 c8"><span class="c22">Note that your content provider should only store the &lt;key, value&gt; pairs local to its own partition.</span></li>
</ol>
<h3 class="c9"><a name="h.ayq6dy9bet3e"></a><span>Step 2: Writing the Main Activity</span></h3>
<p class="c17 c9"><span>The template has an activity used for </span><span class="c11 c26">your own testing and debugging.</span><span> It has three buttons, one button that displays “Test”, one button that displays “LDump” and another button that displays “GDump.” As with the previous assignment, “Test” button is already implemented (it’s the same as “PTest” from the last assignment). You can implement the other two buttons to further test your DHT.</span></p>
<ol class="c3 lst-kix_icgbmnzgjdi-0 start" start="1">
<li class="c4 c17"><span>LDump</span></li>
</ol>
<ol class="c3 lst-kix_icgbmnzgjdi-1 start" start="1">
<li class="c2 c17"><span>When touched, this button should dump and display all the &lt;key, value&gt; pairs </span><span class="c1">stored in your local partition of the node</span><span>.</span></li>
<li class="c2 c17"><span>This means that this button can give “@” as the selection parameter to query().</span></li>
</ol>
<ol class="c3 lst-kix_icgbmnzgjdi-0" start="2">
<li class="c4 c17"><span>GDump</span></li>
</ol>
<ol class="c3 lst-kix_icgbmnzgjdi-1 start" start="1">
<li class="c2 c17"><span>When touched, this button should dump and display all the &lt;key, value&gt; pairs stored in your </span><span class="c1 c19">whole</span><span> DHT. Thus, LDump button is for local dump, and this button (GDump) is for global dump of the entire &lt;key, value&gt; pairs.</span></li>
<li class="c2 c17"><span>This means that this button can give “*” as the selection parameter to query().</span></li>
</ol>
<h4 class="c9 c23"><a name="h.j93vpbbyu58w"></a><span class="c19 c30">Testing</span></h4>
<p class="c10 c9"><span></span></p>
<p class="c9"><span>We have testing programs to help you see how your code does with our grading criteria. If you find any rough edge with the testing programs, please report it on Piazza so the teaching staff can fix it. The instructions are the following:</span></p>
<ol class="c3 lst-kix_pajrhvyy6mn9-0 start" start="1">
<li class="c4"><span>Download a testing program for your platform. If your platform does not run it, please report it on Piazza.</span></li>
</ol>
<ol class="c3 lst-kix_pajrhvyy6mn9-1 start" start="1">
<li class="c2"><span class="c15"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/simpledht-grading.exe&amp;sa=D&amp;usg=AFQjCNH6zDbN_u2B7Jj_Wb2bREqfiBQ_uw">Windows</a></span><span>: We’ve tested it on 32- and 64-bit Windows 8.</span></li>
<li class="c2"><span class="c15"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/simpledht-grading.linux&amp;sa=D&amp;usg=AFQjCNH7MMt3F3S0LdaL1rV_XUx8LuKsyg">Linux</a></span><span>: We’ve tested it on 32- and 64-bit Ubuntu 12.04.</span></li>
<li class="c2"><span class="c15"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/simpledht-grading.osx&amp;sa=D&amp;usg=AFQjCNHp9zF76OLPgCKKMnkxxi4jB_VPGQ">OS X</a></span><span>: We’ve tested it on 32- and 64-bit OS X 10.9 Mavericks.</span></li>
</ol>
<ol class="c3 lst-kix_pajrhvyy6mn9-0" start="2">
<li class="c4"><span>Before you run the program, please make sure that you are running five AVDs. </span><span class="c25">python run_avd.py 5</span><span> will do it.</span></li>
<li class="c4"><span>Run the testing program from the command line.</span></li>
<li class="c4"><span>On your terminal, it will give you your partial and final score, and in some cases, problems that the testing program finds.</span></li>
</ol>
<h3 class="c14 c9"><a name="h.enn5ir8bgmu2"></a><span>Submission</span></h3>
<p class="c9"><span>We use the CSE submit script. You need to use either “</span><span class="c27">submit_cse486” or “submit_cse586”, depending on your registration status.</span><span> If you haven’t used it, the instructions on how to use it is here:</span><span><a class="c6" href="https://www.google.com/url?q=https://wiki.cse.buffalo.edu/services/content/submit-script&amp;sa=D&amp;usg=AFQjCNFyjQ53f5E_2Dq-aJeROWHah33f-w"> </a></span><span class="c15"><a class="c6" href="https://www.google.com/url?q=https://wiki.cse.buffalo.edu/services/content/submit-script&amp;sa=D&amp;usg=AFQjCNFyjQ53f5E_2Dq-aJeROWHah33f-w">https://wiki.cse.buffalo.edu/services/content/submit-script</a></span></p>
<p class="c10 c9"><span></span></p>
<p class="c9"><span>You need to submit one file described below. </span><span class="c22">Once again, you must follow everything below exactly. Otherwise, you will get no point on this assignment.</span></p>
<ul class="c3 lst-kix_or8p6ku1nael-0 start">
<li class="c4"><span>Your entire Eclipse project source code tree zipped up in .zip: The name should be SimpleDht.zip. </span><span class="c22">Please do not change the name.</span><span> To do this, please do the following</span></li>
</ul>
<ol class="c3 lst-kix_or8p6ku1nael-1 start" start="1">
<li class="c2"><span>Open Eclipse.</span></li>
<li class="c2"><span>Go to “File” -&gt; “Export”.</span></li>
<li class="c2"><span>Select “General -&gt; Archive File”.</span></li>
<li class="c2"><span>Select your project. Make sure that you include all the files and check “Save in zip format”.</span></li>
<li class="c2"><span class="c22">Please do not use any other compression tool other than zip, i.e., no 7-Zip, no RAR, etc.</span></li>
</ol>
<h3 class="c9 c14"><a name="h.7tz4jwj9wbbg"></a><span>Deadline: </span><span class="c22">4/11/14 (Friday) 1:59:59pm</span></h3>
<p class="c9"><span>The deadline is firm; if your timestamp is 2pm, it is a late submission.</span></p>
<h3 class="c14 c9"><span>Grading</span></h3>
<p class="c9"><span>This assignment is 15% of your final grade. The breakdown for this assignment is:</span></p>
<ul class="c3 lst-kix_3xka5d8x16z5-0 start">
<li class="c7 c5 c8"><span>1% if local insert/query/delete operations work correctly with 1 AVD.</span></li>
<li class="c7 c5 c8"><span>Additional 3% if the insert operation works correctly with static/stable membership of 5 AVDs.</span></li>
<li class="c7 c5 c8"><span>Additional 3% if the query operation works correctly with static/stable membership of 5 AVDs.</span></li>
<li class="c7 c5 c8"><span>Additional 3% if the insert operation works correctly with 1 - 5 AVDs.</span></li>
<li class="c7 c5 c8"><span>Additional 3% if the query operation works correctly with 1 - 5 AVDs.</span></li>
<li class="c7 c5 c8"><span>Additional 2% if the delete operation works correctly with 1 - 5 AVDs.</span></li>
</ul>
</div>
