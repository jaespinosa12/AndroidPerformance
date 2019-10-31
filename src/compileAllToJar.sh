#!/bin/bash

echo 'Compiling Java'

echo ' - Compile Binary Trees Java'
javac javabenchmarks/binarytrees/*.java
jar -cf generated/BinaryTreesJava.jar javabenchmarks/binarytrees/*.class

echo ' - Compile Fannkuch Redux Java'
javac javabenchmarks/fannkuchredux/*.java
jar -cf generated/FannkuchReduxJava.jar javabenchmarks/fannkuchredux/*.class

echo ' - Compile Fasta Java'
javac javabenchmarks/fasta/*.java
jar -cf generated/FastaJava.jar javabenchmarks/fasta/*.class

echo ' - Compile Mandelbrot Java'
javac javabenchmarks/mandelbrot/*.java
jar -cf generated/MandelbrotJava.jar javabenchmarks/mandelbrot/*.class

echo ' - Compile NBody Java'
javac javabenchmarks/nbody/*.java
jar -cf generated/NBodyJava.jar javabenchmarks/nbody/*.class

echo ' - Compile Spectral Norm Java'
javac javabenchmarks/spectralnorm/*.java
jar -cf generated/SpectralNormJava.jar javabenchmarks/spectralnorm/*.class


echo 'Compiling Kotlin Conv'

echo ' - Compile Binary Trees Kt Converted'
kotlinc -include-runtime -d generated/BinaryTreesKtConv.jar kotlinbenchmarks/conv/binarytrees/*.kt

echo ' - Compile Fannkuch Redux Kt Converted'
kotlinc -include-runtime -d generated/FannkuchReduxKtConv.jar kotlinbenchmarks/conv/fannkuchredux/*.kt

echo ' - Compile Fasta Kt Converted'
kotlinc -include-runtime -d generated/FastaKtConv.jar kotlinbenchmarks/conv/fasta/*.kt

echo ' - Compile Mandelbrot Kt Converted'
kotlinc -include-runtime -d generated/MandelbrotKtConv.jar kotlinbenchmarks/conv/mandelbrot/*.kt

echo ' - Compile NBody Kt Converted'
kotlinc -include-runtime -d generated/NBodyKtConv.jar kotlinbenchmarks/conv/nbody/*.kt

echo ' - Compile Spectral Norm Kt Converted'
kotlinc -include-runtime -d generated/SpectralNormKtConv.jar kotlinbenchmarks/conv/spectralnorm/*.kt
