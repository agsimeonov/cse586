<h2 class="c3 c20"><a name="h.evov0wf199ob"></a><span>CSE 486/586 Distributed Systems</span></h2>
<h2 class="c3 c20"><a name="h.2m784yplpyr3"></a><span>Programming Assignment 2</span></h2>
<h2 class="c3 c16"><a name="h.mu23ds33enb3"></a><span>Totally and Causally Ordered Group Messenger with a Local Persistent Key-Value Table</span></h2>
<p class="c13 c3"><span></span></p>
<h3 class="c3 c23"><a name="h.pvs8jpxxkdjy"></a><span>Update</span></h3>
<p class="c3"><span>You might run into a debugging problem if you&#39;re reinstalling your app from Eclipse. This is because your content provider will still retain previous values even after reinstalling. This won&#39;t be a problem if you uninstall explicitly before reinstalling; uninstalling will delete your content provider storage as well. In order to do this automatically from Eclipse, please refer to the following post on StackOverflow:</span></p>
<p class="c13 c3"><span></span></p>
<p class="c3"><span class="c1"><a class="c8" href="https://www.google.com/url?q=http://stackoverflow.com/a/11106324&amp;sa=D&amp;usg=AFQjCNFQarjKjcq3OJCe87QeU74gw0Dbrg">http://stackoverflow.com/a/11106324</a></span></p>
<h3 class="c23 c3"><a name="h.d85shrjiyret"></a><span>Introduction</span></h3>
<p class="c3"><span>The teaching staff hopes you had fun working on PA1! If you got frustrated, we feel for you and believe us, we were there too. While it is expected to be frustrated in the beginning, we promise you, it will get better and you will enjoy more and more as you do it. You might even start enjoying reading the Android documentation because it *is* actually the single best place to get great information about Android. We do hope, though, that you now understand a bit more about what it means to write networked apps on Android.</span></p>
<p class="c3 c13"><span></span></p>
<p class="c3"><span>Now back to the assignment: this assignment builds on the previous simple messenger and points to the next assignment. You will design a group messenger that preserves total ordering as well as causal ordering of all messages. In addition, you will implement a key-value table that each device uses to individually store all messages on its local storage, which should prep you for the next assignment. </span><span class="c14">Once again, please follow everything exactly. Otherwise, it might result in getting no point for this assignment.</span></p>
<p class="c13 c3"><span></span></p>
<p class="c3"><span>The rest of the description can be long. Please don’t “tl;dr”! Please read to the end first and get the overall picture. Then please revisit as you go!</span></p>
<h3 class="c3"><a name="h.2fozr954o460"></a><span>Step 0: Importing the project template</span></h3>
<p class="c3"><span>Unlike the previous assignment, we will have strict requirements for the UI as well as a few other components. In order to provide you more help in meeting these requirements, we have a project template you can import to Eclipse.</span></p>
<ol class="c5 lst-kix_liqatfti4rdr-0 start" start="1">
<li class="c3 c10"><span>Download </span><span class="c1"><a class="c8" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/GroupMessenger.zip&amp;sa=D&amp;usg=AFQjCNEhZPs8mt5cyDsigT_5Ieqc2Qs3CQ">the project template zip file</a></span><span> to a directory.</span></li>
<li class="c10 c3"><span>Import it to your Eclipse workspace.</span></li>
</ol>
<ol class="c5 lst-kix_liqatfti4rdr-1 start" start="1">
<li class="c9 c3"><span>Open Eclipse.</span></li>
<li class="c9 c3"><span>Go to “File” -&gt; “Import”</span></li>
<li class="c9 c3"><span>Select “General/Existing Projects into Workspace” (</span><span class="c14">Caution</span><span>: this is not “Android/Existing Android Code into Workspace”).</span></li>
<li class="c9 c3"><span>In the next screen (which should be “Import Projects”), do the following:</span></li>
</ol>
<ol class="c5 lst-kix_liqatfti4rdr-2 start" start="1">
<li class="c15 c3"><span>Choose “Select archive file:” and select the project template zip file that you downloaded.</span></li>
<li class="c3 c15"><span>Click “Finish.”</span></li>
</ol>
<ol class="c5 lst-kix_liqatfti4rdr-1" start="5">
<li class="c9 c3"><span>At this point, the project template should have been imported to your workspace.</span></li>
</ol>
<ol class="c5 lst-kix_liqatfti4rdr-2 start" start="1">
<li class="c15 c3"><span>You might get an error saying “Android requires compiler compliance level...” If so, right click on “GroupMessenger” from the Package Explorer, choose “Android Tools” -&gt; “Fix Project Properties” which will fix the error.</span></li>
</ol>
<ol class="c5 lst-kix_liqatfti4rdr-1" start="6">
<li class="c9 c3"><span>Try running it on an AVD and verify that it’s working.</span></li>
</ol>
<ol class="c5 lst-kix_liqatfti4rdr-0" start="3">
<li class="c10 c3"><span>Use the project template for implementing all the components for this assignment.</span></li>
</ol>
<h3 class="c23 c3"><a name="h.7l7zhryu69eh"></a><span>Step 1: Writing a Content Provider</span></h3>
<p class="c3"><span>Your first task is to write a content provider. This provider should be used to store all messages, but the abstraction it provides should be a general key-value table. Before you start, please read the following to understand the basics of a content provider: </span><span class="c1"><a class="c8" href="https://www.google.com/url?q=http://developer.android.com/guide/topics/providers/content-providers.html&amp;sa=D&amp;usg=AFQjCNGX8PtZ77ZyvMd_NlLAxmMGNLEuKA">http://developer.android.com/guide/topics/providers/content-providers.html</a></span></p>
<p class="c13 c3"><span></span></p>
<p class="c3"><span>A typical content provider needs to support (basic) SQL statements. However, you do not need to do it for this course. You will use a content provider as a table storage that stores (key, value) pairs.</span></p>
<p class="c13 c3"><span></span></p>
<p class="c3"><span>The following are the requirements for your provider:</span></p>
<ol class="c5 lst-kix_q0fnac6a6d9d-0 start" start="1">
<li class="c12 c10 c3"><span class="c14">Y</span><span class="c14">ou should not set any permission to access your provider.</span><span> This is very important since if you set a permission to access your content provider, then our testing program cannot test your app. The current template takes care of this; so as long as you do not change the template, you will be fine.</span></li>
<li class="c10 c3"><span>Your provider’s URI should be “content://edu.buffalo.cse.cse486586.groupmessenger.provider”, which means that any app should be able to access your provider using that URI. To simplify your implementation, your provider does not need to match/support any other URI pattern. This is already declared in the project template’s AndroidManifest.xml.</span></li>
<li class="c10 c3"><span>Your provider should have two columns.</span></li>
</ol>
<ol class="c5 lst-kix_q0fnac6a6d9d-1 start" start="1">
<li class="c9 c3"><span>The first column should be named as “key” (an all lowercase string without the quotation marks). This column is used to store all keys.</span></li>
<li class="c12 c9 c3"><span>The second column should be named as “value” (an all lowercase string without the quotation marks). This column is used to store all values.</span></li>
<li class="c12 c9 c3"><span>All keys and values that your provider stores should use the string data type.</span></li>
</ol>
<ol class="c5 lst-kix_q0fnac6a6d9d-0" start="4">
<li class="c10 c3"><span>Your provider should only implement insert() and query(). All other operations are not necessary.</span></li>
<li class="c12 c10 c3"><span>Since the column names are “key” and “value”, any app should be able to insert a &lt;key, value&gt; pair as in the following example:</span></li>
</ol>
<p class="c0"><span></span></p>
<a href="#" name="4baf2ff105f484ed8a95d51304268cb0f7cf7fbb"></a><a href="#" name="0"></a>
<table cellpadding="0" cellspacing="0" class="c18">
<tbody>
<tr class="c28">
<td class="c17" colspan="1" rowspan="1">
<p class="c12 c3 c22"><span class="c7">ContentValues keyValueToInsert = new ContentValues();</span></p>
<p class="c2 c13"><span class="c7"></span></p>
<p class="c2"><span class="c7">// inserting &lt;”key-to-insert”, “value-to-insert”&gt;</span></p>
<p class="c2"><span class="c7">keyValueToInsert.put(“key”, “key-to-insert”);</span></p>
<p class="c2"><span class="c7">keyValueToInsert.put(“value”, “value-to-insert”);</span></p>
<p class="c2 c13"><span class="c7"></span></p>
<p class="c2"><span class="c7">Uri newUri = getContentResolver().insert(</span></p>
<p class="c2"><span class="c7">    providerUri,    // assume we already created a Uri object with our provider URI</span></p>
<p class="c2"><span class="c7">    keyValueToInsert</span></p>
<p class="c2"><span class="c7">);</span></p>
</td>
</tr>
</tbody>
</table>
<p class="c0"><span class="c6 c19"></span></p>
<ol class="c5 lst-kix_q0fnac6a6d9d-0" start="6">
<li class="c12 c10 c3"><span>If there’s a new value inserted using an existing key, you need to keep </span><span class="c14">only the most recent value</span><span>. You should not preserve the history of values under the same key.</span></li>
<li class="c12 c10 c3"><span>Similarly, any app should be able to read a &lt;key, value&gt; pair from your provider with query(). Since your provider is a simple &lt;key, value&gt; table, we are not going to follow the Android convention; your provider should be able to answer queries as in the following example:</span></li>
</ol>
<p class="c0"><span></span></p>
<a href="#" name="e12a0dc5aa0c968c01edfcb7eb1bbdf55d956f91"></a><a href="#" name="1"></a>
<table cellpadding="0" cellspacing="0" class="c18">
<tbody>
<tr class="c28">
<td class="c17" colspan="1" rowspan="1">
<p class="c2"><span class="c7">Cursor resultCursor = getContentResolver().query(</span></p>
<p class="c2"><span class="c7">    providerUri,    // assume we already created a Uri object with our provider URI</span></p>
<p class="c2"><span>    null,                // no need to support the </span><span class="c6">projection </span><span class="c7">parameter</span></p>
<p class="c2"><span>    “key-to-read”,    // we provide the key directly as the </span><span class="c6">selection</span><span class="c7"> parameter</span></p>
<p class="c2"><span>    null,                // no need to support the </span><span class="c6">selectionArgs</span><span class="c7"> parameter</span></p>
<p class="c2"><span>    null                 // no need to support the </span><span class="c6">sortOrder </span><span class="c7">parameter</span></p>
<p class="c2"><span class="c7">);</span></p>
</td>
</tr>
</tbody>
</table>
<p class="c0 c22"><span></span></p>
<p class="c12 c22 c3"><span>Thus, your query() implementation should read the </span><span class="c6">selection</span><span> parameter and use it as the key to retrieve from your table. In turn, the Cursor returned by your query() implementation should include only one row with two columns using your provider’s column names, i.e., “key” and “value”. You probably want to use android.database.MatrixCursor instead of implementing your own Cursor.</span></p>
<ol class="c5 lst-kix_q0fnac6a6d9d-0" start="8">
<li class="c12 c10 c3"><span>Your provider should store the &lt;key, value&gt; pairs using one of the data storage options. The details of possible data storage options are in </span><span class="c1"><a class="c8" href="https://www.google.com/url?q=http://developer.android.com/guide/topics/data/data-storage.html&amp;sa=D&amp;usg=AFQjCNGKdyvVC7yavKLs4CsxqtpHPrRRvg">http://developer.android.com/guide/topics/data/data-storage.html</a></span><span>. You can choose any option; however, the easiest way to do this is probably use the internal storage with the key as the file name and the value stored in the file.</span></li>
<li class="c12 c10 c3"><span>After implementing your provider, you can verify whether or not you are meeting the requirements by clicking “PTest” provided in the template. You can take a look at OnPTestClickListener.java to see what tests it does.</span></li>
<li class="c12 c10 c3"><span>If your provider does not pass PTest, there will be no point for this portion of the assignment.</span></li>
</ol>
<h3 class="c3"><a name="h.31dlm68eonzx"></a><span>Step 2: Implementing Total-Causal Ordering</span></h3>
<p class="c3"><span>The final step is supporting both total and causal ordering. You will need to design an algorithm that does this and implement it.</span><span class="c14 c6 c19"> </span><span>The requirements are:</span></p>
<ol class="c5 lst-kix_shspgy4e04lv-0 start" start="1">
<li class="c10 c3"><span>Your app should multicast every user-entered message to all app instances (</span><span class="c11">including the one that is sending the message</span><span>). </span><span class="c14">In the rest of the description, “multicast” always means sending a message to all app instances.</span></li>
<li class="c10 c3"><span>Your app should u</span><span>se B-multicast.</span><span> It should not implement R-multicast.</span></li>
<li class="c12 c10 c3"><span class="c11">You need to come up with an algorithm that provides a total-causal ordering. </span><span>After all, this is the main point of this assignment!</span></li>
<li class="c12 c10 c3"><span>As with PA1, we have fixed the ports &amp; sockets.</span></li>
</ol>
<ol class="c5 lst-kix_shspgy4e04lv-1 start" start="1">
<li class="c12 c9 c3"><span>Your app should open one server socket that listens on 10000.</span></li>
<li class="c12 c9 c3"><span>You need to use </span><span>run_avd.py</span><span> and </span><span>set_redir.py</span><span> to set up the testing environment.</span></li>
<li class="c12 c9 c3"><span>The grading will use 5 AVDs. The redirection ports are 11108, 11112, 11116, 11120, and 11124.</span></li>
<li class="c9 c3 c12"><span>You should just hard-code the above 5 ports and use them to set up connections.</span></li>
<li class="c12 c9 c3"><span>Please use the code snippet provided in PA1 on how to determine your local AVD.</span></li>
</ol>
<ol class="c5 lst-kix_shspgy4e04lv-2 start" start="1">
<li class="c4 c3"><span>emulator-5554: “5554”</span></li>
<li class="c4 c3"><span>emulator-5556: “5556”</span></li>
<li class="c3 c4"><span>emulator-5558: “5558”</span></li>
<li class="c4 c3"><span>emulator-5560: “5560”</span></li>
<li class="c4 c3"><span>emulator-5562: “5562”</span></li>
</ol>
<ol class="c5 lst-kix_shspgy4e04lv-0" start="5">
<li class="c12 c10 c3"><span>Every message should be stored in your provider individually by all app instances. Each message should be stored as a &lt;key, value&gt; pair. The key should be the final delivery sequence number for the message (as a string); the value should be the actual message (again, as a string). The delivery sequence number should start from 0 and increase by 1 for each message.</span></li>
<li class="c12 c10 c3"><span>For your debugging purposes, you can display all the messages on the screen. However, there is no grading component for this.</span></li>
</ol>
<h4 class="c3 c21"><a name="h.j93vpbbyu58w"></a><span class="c19 c25">Testing</span></h4>
<p class="c13 c3"><span></span></p>
<p class="c3"><span>We have testing programs to help you see how your code does with our grading criteria. If you find any rough edge with the testing programs, please report it on Piazza so the teaching staff can fix it. The instructions are the following:</span></p>
<ol class="c5 lst-kix_r2mjvp1pk0yz-0 start" start="1">
<li class="c10 c3"><span>Download a testing program for your platform. If your platform does not run it, please report it on Piazza.</span></li>
</ol>
<ol class="c5 lst-kix_r2mjvp1pk0yz-1 start" start="1">
<li class="c9 c3"><span class="c1"><a class="c8" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/groupmessenger-grading.exe&amp;sa=D&amp;usg=AFQjCNG8awEbSgLB8IplHj-yW84_-JpwDQ">Windows</a></span><span>: We’ve tested it on 32- and 64-bit Windows 8.</span></li>
<li class="c3 c9"><span class="c1"><a class="c8" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/groupmessenger-grading.linux&amp;sa=D&amp;usg=AFQjCNFsdoMINkVSe_yMjJ4Dp51qhfW-SQ">Linux</a></span><span>: We’ve tested it on 32- and 64-bit Ubuntu 12.04.</span></li>
<li class="c9 c3"><span class="c1"><a class="c8" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/groupmessenger-grading.osx&amp;sa=D&amp;usg=AFQjCNGZABL5xlMlSjBJVWyKESP9VrooYQ">OS X</a></span><span>: We’ve tested it on 32- and 64-bit OS X 10.9 Mavericks.</span></li>
</ol>
<ol class="c5 lst-kix_r2mjvp1pk0yz-0" start="2">
<li class="c10 c3"><span>Before you run the program, please make sure that you are running five AVDs. </span><span class="c29">python run_avd.py 5</span><span> will do it.</span></li>
<li class="c10 c3"><span>Please also make sure that you have installed your GroupMessenger on all the AVDs.</span></li>
<li class="c10 c3"><span>Run the testing program from the command line.</span></li>
<li class="c10 c3"><span>On your terminal, it will give you your partial and final score, and in some cases, problems that the testing program finds.</span></li>
</ol>
<h3 class="c3"><a name="h.enn5ir8bgmu2"></a><span>Submission</span></h3>
<p class="c3"><span>We use the CSE submit script. You need to use either “</span><span class="c26">submit_cse486” or “submit_cse586”, depending on your registration status.</span><span> If you haven’t used it, the instructions on how to use it is here:</span><span><a class="c8" href="https://www.google.com/url?q=https://wiki.cse.buffalo.edu/services/content/submit-script&amp;sa=D&amp;usg=AFQjCNFyjQ53f5E_2Dq-aJeROWHah33f-w"> </a></span><span class="c1"><a class="c8" href="https://www.google.com/url?q=https://wiki.cse.buffalo.edu/services/content/submit-script&amp;sa=D&amp;usg=AFQjCNFyjQ53f5E_2Dq-aJeROWHah33f-w">https://wiki.cse.buffalo.edu/services/content/submit-script</a></span></p>
<p class="c13 c3"><span></span></p>
<p class="c3"><span>You need to submit one file described below. </span><span class="c14">Once again, you must follow everything below exactly. Otherwise, you will get no point on this assignment.</span></p>
<ul class="c5 lst-kix_isr34vownjvq-0 start">
<li class="c10 c3"><span>Your entire Eclipse project source code tree zipped up in .zip: The name should be GroupMessenger.zip. </span><span class="c14">Please do not change the name.</span><span> To do this, please do the following</span></li>
</ul>
<ol class="c5 lst-kix_isr34vownjvq-1 start" start="1">
<li class="c9 c3"><span>Open Eclipse.</span></li>
<li class="c9 c3"><span>Go to “File” -&gt; “Export”.</span></li>
<li class="c9 c3"><span>Select “General -&gt; Archive File”.</span></li>
<li class="c9 c3"><span>Select your project. Make sure that you include all the files and check “Save in zip format”.</span></li>
<li class="c9 c3"><span class="c14">Please do not use any other compression tool other than zip, i.e., no 7-Zip, no RAR, etc.</span></li>
</ol>
<h3 class="c3"><a name="h.7tz4jwj9wbbg"></a><span>Deadline: </span><span class="c14">3/7/14 (Friday) 1:59:59pm</span></h3>
<p class="c3"><span>The deadline is firm; if your timestamp is 2pm, it is a late submission.</span></p>
<h3 class="c3"><a name="h.6nv5hzcj5681"></a><span>Grading</span></h3>
<p class="c3"><span>This assignment is 15% of your final grade. The breakdown for this assignment is:</span></p>
<ul class="c5 lst-kix_qyjicynd75pe-0 start">
<li class="c10 c3"><span>5% if the content provider behaves correctly</span></li>
<li class="c10 c3"><span>5% if total ordering is preserved</span></li>
<li class="c10 c3"><span>(additional) 5% if total </span><span class="c6">and</span><span> causal ordering is preserved</span></li>
</ul>
</div>
