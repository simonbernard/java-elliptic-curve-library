Elliptic curve crypto library on Java MicroEdition
===========================

This repository hosts the source code of my Bachelor thesis at university 2006. I was implementing a library with some basic algorithms to implement higher level crypto algorithms. The goal was to make it portable AND faster than what was existing at that time.

The easiest way to make it portable was to implement it in Java (Micro Edition). So I made some tests using the Java BigInteger lib. It was quite disappointing. What I needed was a implementation of basic arithmetic operations optimized for finite fields. I did not find any on the web, so I had to implement myself. This caused me some headache but it was finally worth the effort.

The state of the art library these days was Bouncy Castle (maybe it still is but I’m not up-to-date). So I used this to compare the efficiency of my own implementation and guess the result: It got much faster.
