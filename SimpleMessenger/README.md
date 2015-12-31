<h3 class="c22 c10"><a name="h.51kqey17787"></a><span>CSE 486/586 Distributed Systems</span></h3>
<h3 class="c10 c22"><a name="h.2hk75lk5uqk8"></a><span>Programming Assignment 1</span></h3>
<h3 class="c22 c10 c26"><a name="h.2hk75lk5uqk8"></a><span>Simple Messenger on Android</span></h3>
<h4 class="c10"><a name="h.3ntd4a5soub2"></a><span>Update 1</span></h4>
<ul class="c8 lst-kix_5djqkuoq2oic-0 start">
<li class="c17 c10"><span>Please do not use Android Studio. It does not work with the current setup.</span></li>
<li class="c17 c10"><span>Please checkout the testing section. There are testing programs you can download and test your code with.</span></li>
</ul>
<h4 class="c10"><a name="h.2iypr0alhp9x"></a><span>Introduction</span></h4>
<p class="c10"><span>In this assignment, you will write a simple messenger app on Android. The goal of this app is simple: enabling two Android devices to send messages to each other. The purpose of this assignment is to help you see if you have the right background for this course. </span><span class="c9">If you can finish this all by yourself without getting any help from others</span><span>, then it is probably the case that you have the right background.</span><span> </span><span>Please see for yourself!</span></p>
<p class="c3 c24"><span></span></p>
<p class="c3"><span>There are four high-level challenges in this assignment:</span></p>
<ul class="c8 lst-kix_h5lmsncm5k8y-0 start">
<li class="c1 c7"><span class="c15">There will be a lot of reading involved in this assignment.</span><span> This is because you need to read many tutorials/articles online.</span></li>
<li class="c1 c7"><span class="c15">There will be a lot of infrastructure setup.</span><span> This involves installing various software components and configuring them.</span></li>
<li class="c1 c7"><span class="c15">There will be many trials and errors to make Android do what you want to do.</span><span> This is true not just for Android but also for any platform/framework. Luckily, Android provides excellent official documentation. You will need to get used to reading the official developer website.</span></li>
<li class="c1 c7"><span class="c15">There are networking restrictions that the Android emulator environment imposes.</span><span> You need to get used to it for the assignment.</span></li>
</ul>
<p class="c3 c24"><span></span></p>
<p class="c3"><span>So here we go!</span></p>
<h4 class="c10"><a name="h.19xyd3q1085a"></a><span>Step 1: Getting Started</span></h4>
<ul class="c8 lst-kix_6rpxbzjbycr1-0 start">
<li class="c1 c7"><span>Unless you are already familiar with Android programming, the first thing you want to do is to follow the </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/training/index.html&amp;sa=D&amp;usg=AFQjCNGNjjc5Cy20ZzQ98_azu3MctpKvag">tutorials</a></span><span>.</span></li>
<li class="c1 c7"><span>Please follow the first tutorial </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/training/basics/firstapp/index.html&amp;sa=D&amp;usg=AFQjCNGtJa32X0jbdB0XvRtAHRO2HyvBvQ">“Building Your First App”</a></span><span> which will guide you through the process of installing necessary software and creating a simple app.</span></li>
</ul>
<ul class="c8 lst-kix_6rpxbzjbycr1-1 start">
<li class="c3 c5"><span class="c9">Important Note 1: </span><span>Please use Eclipse. </span><span class="c9">Android Studio does not work with the current set up.</span></li>
<li class="c3 c5"><span class="c9">Important Note 2</span><span>: There is one part of this tutorial where it instructs you to create an Android emulator instance (or AVD, Android Virtual Device). Please follow </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/tools/devices/emulator.html%23accel-vm&amp;sa=D&amp;usg=AFQjCNEQvdwKVw41NQavALtpzqjQW-Yjyw">this</a></span><span> to create an x86-based AVD, </span><span class="c9">not</span><span> an ARM-based AVD. x86-based AVDs run a lot faster with less resources, so it’s going to be easier to run on your laptop. However, it is not a requirement for you to use x86-based AVDs. If your machine doesn’t support VT-x, you can still use ARM-based AVDs.</span></li>
<li class="c3 c5"><span>If you are a Mac OS X Mavericks (10.9) user, there is a bug in the original HAXM. Please download and install this </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://software.intel.com/sites/default/files/haxm-macosx_r03_hotfix.zip&amp;sa=D&amp;usg=AFQjCNGrecyrlUdw5QNPgE-GbvmiQW-5RA">“hotfix”</a></span><span> instead.</span></li>
</ul>
<ul class="c8 lst-kix_6rpxbzjbycr1-0">
<li class="c1 c7"><span>Please follow the fourth tutorial </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/training/basics/activity-lifecycle/index.html&amp;sa=D&amp;usg=AFQjCNE6NyuX-0A6ykgiCb8RULiRj4T3QQ">“Managing the Activity Lifecycle”</a></span><span> which will give you some basic concepts to understand the code given (as described in Step 2 below).</span></li>
<li class="c1 c7"><span>For more information on Android programming, please refer to the following pages.</span></li>
</ul>
<ul class="c8 lst-kix_6rpxbzjbycr1-1 start">
<li class="c3 c5"><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/guide/components/fundamentals.html&amp;sa=D&amp;usg=AFQjCNELbsWWIopoSXwniNw7eAARZeOUQQ">Application Fundamentals</a></span><span> </span></li>
<li class="c3 c5"><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/guide/components/activities.html&amp;sa=D&amp;usg=AFQjCNFv6RXmyqkYNZp49ie3Etj_hAm52A">Activities</a></span></li>
</ul>
<ul class="c8 lst-kix_nradyi8f34l2-1 start">
<li class="c3 c5"><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/guide/components/processes-and-threads.html&amp;sa=D&amp;usg=AFQjCNH--mLJhTvCS2ZUedYnrtMpLUoPEQ">Processes and Threads</a></span></li>
</ul>
<ul class="c8 lst-kix_nradyi8f34l2-0 start">
<li class="c1 c7"><span class="c9">For the success of all the programming assignments, it is critical</span><span> </span><span>that you know how to use the Android emulator and debug your app because you will spend lots of time using the emulator and debugging. The following pages will give you the information on debugging.</span></li>
</ul>
<ul class="c8 lst-kix_nradyi8f34l2-1 start">
<li class="c3 c5"><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/tools/devices/emulator.html&amp;sa=D&amp;usg=AFQjCNGwKyzd7xpQ1IjFXM1-H8j6ADiWWA">Using the Android Emulator</a></span></li>
<li class="c3 c5"><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/tools/debugging/index.html&amp;sa=D&amp;usg=AFQjCNGsD1nSGVD4W0vIEa9wRxoecxAIfg">Debugging</a></span><span>, especially </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/tools/debugging/debugging-projects.html&amp;sa=D&amp;usg=AFQjCNHtdIbwwSlVhFN-hxw_SNdmyiLDaw">with Eclipse</a></span><span> </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/tools/debugging/debugging-log.html&amp;sa=D&amp;usg=AFQjCNFce5UDh_v2FYm1pmPxwAjofdOwFA">using the Log class</a></span><span>.</span></li>
</ul>
<h4 class="c10"><a name="h.gjqbzg9enhuu"></a><span>Step 2: Setting up a Testing Environment</span></h4>
<p class="c19 c10"><span>You will need to run two AVDs in order to test your app. Unfortunately, Android does not provide a flexible networking environment for AVDs, so there are some hurdles to jump over in order to set up the right environment. The following are the instructions. Although the instructions should work on Windows platforms, we suggest you use a UNIX-based platform, e.g., Linux or Mac, because the teaching staff may not use Windows and the support will be limited.</span></p>
<ul class="c8 lst-kix_h4pk7sf4jxi3-0 start">
<li class="c1"><span>You need to have the Android SDK and Python 2.x (</span><span class="c9">not</span><span> 3.x; Python 3.x versions are </span><span class="c9">not</span><span> compatible with the scripts provided.) installed on your machine. </span><span>If you have not installed these, please do it first and proceed to the next step.</span></li>
<li class="c1"><span>Add &lt;your Android SDK directory&gt;/tools to your $PATH so you can run Android’s development tools anywhere.</span></li>
</ul>
<ul class="c8 lst-kix_h4pk7sf4jxi3-1 start">
<li class="c0 c19"><span>A good reference on how to change $PATH is </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://www.java.com/en/download/help/path.xml&amp;sa=D&amp;usg=AFQjCNHRN4c-0CfMo-A_UIDPEEBGeMTDZQ">here</a></span><span>.</span></li>
</ul>
<ul class="c8 lst-kix_h4pk7sf4jxi3-0">
<li class="c1"><span>Add &lt;your Android SDK directory&gt;/platform-tools to your $PATH so you can run Android’s platform tools anywhere.</span></li>
<li class="c1"><span>Download and save </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/create_avd.py&amp;sa=D&amp;usg=AFQjCNGsCxwyIcj4917sXuCib6nYM7TSyg">create_avd.py</a></span><span>, </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/run_avd.py&amp;sa=D&amp;usg=AFQjCNEwl-3F1Zh8zK3Rseaje6-Rc1S-BQ">run_avd.py</a></span><span>, and </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/set_redir.py&amp;sa=D&amp;usg=AFQjCNFuaB3-DnI51qu_xrr2DoIWnaHWTA">set_redir.py</a></span><span>.</span></li>
<li class="c1"><span>To create AVDs, enter: </span><span class="c2">python create_avd.py 5</span></li>
</ul>
<ul class="c8 lst-kix_h4pk7sf4jxi3-1 start">
<li class="c0 c19"><span>The above command should be executed only once because you do not need to create AVDs multiple times.</span></li>
<li class="c0 c19"><span>When asked the question “Do you wish to create a custom hardware profile [no]”, just press enter.</span></li>
<li class="c0 c19"><span>5 AVDs should have been created by the above command. The names are avd0, avd1, avd2, avd3, &amp; avd4. You can manipulate them in Eclipse, but please do not edit or delete them because they are necessary for our scripts to work.</span></li>
<li class="c0 c19"><span>If you can’t create x86-based AVDs, please enter: </span><span class="c2">python create_avd.py 5 arm</span><span> instead; this will create ARM-based AVDs.</span></li>
</ul>
<ul class="c8 lst-kix_h4pk7sf4jxi3-0">
<li class="c1"><span>For all the programming assignments except this first one, </span><span class="c9">you will need to run 5 AVDs at the same time</span><span>. This means that you need to have access to a machine that can handle 5 AVDs running simultaneously.</span></li>
<li class="c1"><span>In order to test the above, please enter: </span><span class="c2">python run_avd.py 5</span></li>
</ul>
<ul class="c8 lst-kix_h4pk7sf4jxi3-1 start">
<li class="c0 c19"><span>The above command will launch 5 AVDs.</span></li>
<li class="c0 c19"><span>Please play around with all 5 AVDs and make sure that your machine can handle them comfortably. Most of the recent laptops will run 5 AVDs without much difficulty.</span></li>
<li class="c0 c19"><span>After you are done checking, close all AVDs.</span></li>
</ul>
<ul class="c8 lst-kix_h4pk7sf4jxi3-0">
<li class="c1"><span>For this assignment you need two AVDs, so enter: </span><span class="c2">python run_avd.py 2</span></li>
</ul>
<ul class="c8 lst-kix_h4pk7sf4jxi3-1 start">
<li class="c0 c19"><span>The above command will start two AVDs, avd0 &amp; avd1.</span></li>
<li class="c0 c19"><span>In general, to run the AVDs created by create_avd.py, enter: </span><span class="c2">python run_avd.py &lt;number of AVDs&gt;</span></li>
</ul>
<ul class="c8 lst-kix_h4pk7sf4jxi3-0">
<li class="c1"><span>After all AVDs finish launching, create a network that connects the AVDs by entering: </span><span class="c2">python set_redir.py 10000</span></li>
</ul>
<ul class="c8 lst-kix_h4pk7sf4jxi3-1 start">
<li class="c0 c19"><span>The above command will set up port redirections, but there are some restrictions in terms of socket programming. We will detail the restrictions in Step 3 below.</span></li>
</ul>
<h4 class="c10"><a name="h.4ko6prwmy0li"></a><span>Step 3: Writing the Messenger App</span></h4>
<p class="c3"><span>The actual implementation is writing the messenger app. For this, we have a project template you can import to Eclipse.</span></p>
<ol class="c8 lst-kix_83zss28nmcpn-0 start" start="1">
<li class="c17 c10"><span>Download </span><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/SimpleMessenger.zip&amp;sa=D&amp;usg=AFQjCNE_-vNBeTCPV8nAqINTsMlKsNeYCg">the project template zip file</a></span><span> to a directory.</span></li>
<li class="c17 c10"><span>Import it to your Eclipse workspace.</span></li>
</ol>
<ol class="c8 lst-kix_83zss28nmcpn-1 start" start="1">
<li class="c0"><span>Open Eclipse.</span></li>
<li class="c0"><span>Go to “File” -&gt; “Import”</span></li>
<li class="c0"><span>Select “General -&gt; Existing Projects into Workspace” (</span><span class="c9">Caution</span><span>: this is </span><span class="c9">not</span><span> “Android -&gt; Existing Android Code into Workspace”).</span></li>
<li class="c0"><span>In the next screen (which should be “Import Projects”), do the following:</span></li>
</ol>
<ol class="c8 lst-kix_83zss28nmcpn-2 start" start="1">
<li class="c21 c10"><span>Choose “Select archive file:” and select the project template zip file that you downloaded.</span></li>
<li class="c21 c10"><span>Click “Finish.”</span></li>
</ol>
<ol class="c8 lst-kix_83zss28nmcpn-1" start="5">
<li class="c0"><span>At this point, the project template should have been imported to your workspace.</span></li>
</ol>
<ol class="c8 lst-kix_83zss28nmcpn-2 start" start="1">
<li class="c10 c21"><span>You might get an error saying “Android requires compiler compliance level...” If so, right click on “SimpleMessenger” from the Package Explorer, choose “Android Tools” -&gt; “Fix Project Properties” which will fix the error.</span></li>
</ol>
<ol class="c8 lst-kix_83zss28nmcpn-1" start="6">
<li class="c0"><span>Try running it on an AVD and verify that it’s working.</span></li>
</ol>
<ol class="c8 lst-kix_83zss28nmcpn-0" start="3">
<li class="c17 c10"><span>Add your code to the project template for this assignment. If you look at the template code, there are a few “TODO” sections. Those are the places where you need to add your code.</span></li>
<li class="c17 c10"><span>Before implementing anything, please understand the template code first.</span></li>
</ol>
<ol class="c8 lst-kix_83zss28nmcpn-1 start" start="1">
<li class="c0"><span>The main Activity is in SimpleMessengerActivity.java.</span></li>
<li class="c0"><span>Please read the code and the comments carefully.</span></li>
</ol>
<p class="c3 c24"><span></span></p>
<p class="c3"><span>The project requirements are below. </span><span class="c15">You must follow everything below exactly. Otherwise, you will get no point on this assignment.</span></p>
<ol class="c8 lst-kix_qa3dxitv31yj-0 start" start="1">
<li class="c1 c7"><span>There should be only one app that you develop and need to install for grading. If you just use the project template and add your code there, you will be able to satisfy this requirement.</span></li>
<li class="c1 c7"><span>When creating your new Android application project in Eclipse, use the following:</span></li>
</ol>
<ul class="c8 lst-kix_qa3dxitv31yj-1 start">
<li class="c3 c5"><span>Application Name: SimpleMessenger</span></li>
<li class="c3 c5"><span>Project Name: SimpleMessenger</span></li>
<li class="c3 c5"><span>Package Name: edu.buffalo.cse.cse486586.simplemessenger</span></li>
<li class="c3 c5"><span>API level 19 as the minimum &amp; target SDK.</span></li>
<li class="c3 c5"><span>If you just use the project template, you will be able to satisfy this requirement.</span></li>
</ul>
<ol class="c8 lst-kix_qa3dxitv31yj-0" start="3">
<li class="c1 c7"><span>There should be only one text box on screen where a</span><span> user of the device can write a text message to the other device. If you just use the project template, you will satisfy this requirement.</span></li>
<li class="c1 c7"><span>The other device should be able to display on screen what was received and vice versa. The project template contains basic code for displaying messages on screen.</span></li>
<li class="c1 c7"><span>You need to use the Java Socket API.</span></li>
<li class="c1 c7"><span>All communication should be over TCP.</span></li>
<li class="c1 c7"><span>You can assume that the size of a message will never exceed 128 bytes (characters).</span></li>
</ol>
<p class="c3 c24"><span></span></p>
<p class="c3"><span>As mentioned above, the Android emulator environment is not flexible for networking among multiple AVDs. Although set_redir.py enables networking among multiple AVDs, it is very different from a typical networking setup. When you write your socket code, you have the following restrictions:</span></p>
<ul class="c8 lst-kix_9umvon2h16on-0 start">
<li class="c1 c7"><span>In your app, you can open only one server socket that listens on port 10000 regardless of which AVD your app runs on.</span></li>
<li class="c1 c7"><span>The app on avd0 can connect to the listening server socket of the app on avd1 by connecting to &lt;ip&gt;:&lt;port&gt; == 10</span><span>.0.2.2:11112.</span></li>
<li class="c1 c7"><span>The app on avd1 can connect to the listening server socket of the app on avd0 by connecting to &lt;ip&gt;:&lt;port&gt; == 10.0.2.2:11108.</span></li>
<li class="c1 c7"><span>Your app knows which AVD it is running on via the following code snippet. If portStr is “5554”, then it is avd0. If portStr is “5556”, then it is avd1:</span></li>
</ul>
<a href="#" name="cd8bd2105802606adbbffba028ea26b0f0bf5ce7"></a><a href="#" name="0"></a>
<table cellpadding="0" cellspacing="0" class="c11">
<tbody>
<tr class="c13">
<td class="c2 c27" colspan="1" rowspan="1">
<p class="c3"><span class="c12">TelephonyManager tel =</span></p>
<p class="c3"><span>        (Telephon</span><span>yManager) </span><span>this.getSystemService(Context.TELEPHONY_SERVICE);</span></p>
<p class="c3"><span class="c12">String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);</span></p>
</td>
</tr>
</tbody>
</table>
<ul class="c8 lst-kix_9umvon2h16on-0">
<li class="c1 c7"><span>The project template already implements the above, but you are expected to understand the code as this is critical for the rest of the programming assignments.</span></li>
<li class="c1 c7"><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://developer.android.com/tools/devices/emulator.html%23emulatornetworking&amp;sa=D&amp;usg=AFQjCNERfLcNK9k4cn2gJswv0Tcpel_JJg">This document</a></span><span> explains the Android emulator networking environment in more detail.</span></li>
<li class="c1 c7"><span>In general, set_redir.py creates an emulated, port-redirected network like this (VR stands for Virtual Router):</span></li>
</ul>
<p class="c3 c25"><span style="overflow: hidden; display: inline-block; margin: 0.00px 0.00px; border: 0.00px solid #000000; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 467.00px; height: 257.00px;"><img alt="" src="https://lh3.googleusercontent.com/tco3xY8YKFc2EHJx-LKb79kLruQWoJfowf-_AfSQFUGU_UUvooAlm2asBCdZKupKbes6stvpJc-CU6xDRtBGpH1laBs68HF6lAaxRcits7LBTEyhDtfziHUobdcVv_Ni-9GGpYo" style="width: 467.00px; height: 257.00px; margin-left: 0.00px; margin-top: 0.00px; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px);" title=""></span></p>
<h4 class="c10"><a name="h.c0g4y6xqc02u"></a><span>Testing</span></h4>
<p class="c10"><span>We have testing programs to help you see how your code does with our grading criteria. If you find any rough edge with the testing programs, please report it on Piazza so the teaching staff can fix it. The instructions are the following:</span></p>
<ol class="c8 lst-kix_vnf907e47vvw-0 start" start="1">
<li class="c17 c10"><span>Download a testing program for your platform. If your platform does not run it, please report it on Piazza.</span></li>
</ol>
<ol class="c8 lst-kix_vnf907e47vvw-1 start" start="1">
<li class="c0"><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/simplemessenger-grading.exe&amp;sa=D&amp;usg=AFQjCNGMTg_KRo4dlGY3gfPQMuUzK2WmdA">Windows</a></span><span>: We’ve tested it on 64-bit Windows 8.</span></li>
<li class="c0"><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/simplemessenger-grading.linux&amp;sa=D&amp;usg=AFQjCNGtKF6n5q7Mjq3L_3P2KsNoQWhulg">Linux</a></span><span>: We’ve tested it on 64-bit Ubuntu 12.04.</span></li>
<li class="c0"><span class="c4"><a class="c6" href="https://www.google.com/url?q=http://www.cse.buffalo.edu/~stevko/courses/cse486/spring14/files/simplemessenger-grading.osx&amp;sa=D&amp;usg=AFQjCNGnVv7Kku1fXcGE38YHuNN0PzEjBA">OS X</a></span><span>: We’ve tested it on 64-bit OS X 10.9 Mavericks.</span></li>
</ol>
<ol class="c8 lst-kix_vnf907e47vvw-0" start="2">
<li class="c17 c10"><span>Before you run the program, please make sure that you are running two AVDs (avd0 &amp; avd1). </span><span class="c2">python run_avd.py 2</span><span> will do it.</span></li>
<li class="c17 c10"><span>Please also make sure that you have installed your SimpleMessenger on those two AVDs.</span></li>
<li class="c10 c17"><span>Run the testing program from the command line.</span></li>
<li class="c17 c10"><span>At the end of the run, it will give you one of the three outputs.</span></li>
</ol>
<ol class="c8 lst-kix_vnf907e47vvw-1 start" start="1">
<li class="c0"><span>No communication verified: if SimpleMessenger instances cannot communicate with each other. This is 0 point.</span></li>
<li class="c0"><span>One-way communication verified: if SimpleMessenger on avd0 can send a message to SimpleMessenger on avd1. This is 2 points.</span></li>
<li class="c0"><span>Two-way communication verified: if both AVDs can communicate with each other. This is additional 3 points.</span></li>
</ol>
<h4 class="c10"><a name="h.216ssivue5c7"></a><span>Submission</span></h4>
<p class="c3"><span>We use the CSE submit script. You need to use either “</span><span class="c16">submit_cse486” or “submit_cse586”, depending on your registration status.</span><span> If you haven’t used it, the instructions on how to use it is here: </span><span class="c14"><a class="c6" href="https://www.google.com/url?q=https://wiki.cse.buffalo.edu/services/content/submit-script&amp;sa=D&amp;usg=AFQjCNFyjQ53f5E_2Dq-aJeROWHah33f-w">https://wiki.cse.buffalo.edu/services/content/submit-script</a></span></p>
<p class="c3 c24"><span></span></p>
<p class="c3"><span>You need to submit one file described below. </span><span class="c9">Once again, you must follow everything below exactly. Otherwise, you will get no point on this assignment.</span></p>
<ul class="c8 lst-kix_l9x6vazbmtfw-0 start">
<li class="c1 c7"><span>Your entire Eclipse project source code tree zipped up in .zip: The name should be SimpleMessenger.zip.</span><span> To do this, please do the following</span></li>
</ul>
<ol class="c8 lst-kix_l9x6vazbmtfw-1 start" start="1">
<li class="c3 c5"><span>Open Eclipse.</span></li>
<li class="c3 c5"><span>Go to “File” -&gt; “Export”.</span></li>
<li class="c3 c5"><span>Select “General -&gt; Archive File”.</span></li>
<li class="c3 c5"><span>Select your project. Make sure that you include all the files and check “Save in zip format”.</span></li>
<li class="c3 c5"><span class="c9">Please do not use any other compression tool other than zip, i.e., no 7-Zip, no RAR, etc.</span></li>
</ol>
<h4 class="c10 c18"><a name="h.wemjwwjomibj"></a><span>Deadline: </span><span class="c9">2/3/14 (Monday) 1:59:59pm</span></h4>
<p class="c3"><span>This is right before 2pm. The deadline is firm; if your timestamp is 2pm, it is a late submission.</span></p>
<h4 class="c18 c10"><a name="h.ers11l4p7ldz"></a><span>Grading</span></h4>
<p class="c3"><span>This assignment is 5% of your final grade. The breakdown for this assignment is:</span></p>
<ul class="c8 lst-kix_or3emn22ns7-0 start">
<li class="c1 c7"><span>2%: if your messenger app can send one-way messages from avd0 to avd1.</span></li>
<li class="c1 c7"><span>(additional) 3%: if your messenger app can send two-way messages between avd0 and avd1.</span></li>
</ul>
</div>
