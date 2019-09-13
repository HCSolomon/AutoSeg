# AutoSeg
An Insight project on building an automated image segmentation pipeline for machine learning training sets.

## Tech Stack

HDFS

Spark

## Data Source

OpenImages (500GB)

## Engineering Challenge

Large-scale image classification itself is tasking due to the need to preprocess images before being used for training and validating. The scale becomes even larger when segmenting images into masks. Such processing on a single machine for an entire dataset takes a lot of time and there is too much room for failure within these periods. But by scaling out computations, I will have to determine ways to make the process fault-tolerant.

## Business Value

Computer vision is an increasingly popular subject in health science, manufacturing, and automotive among other industries. Many organizations have already begun implementing this technology into their products. But before their ML model was trained, it needed training data. Manually obtained training data. The process of segmenting, categorizing, and labelling data is so labor intensive that many companies have turned to outsourcing this task (Google, for example, uses their reCAPTCHA API to do this: https://www.google.com/recaptcha/intro/v3.html). However, by automating this task, engineers and researchers will be able to get more specific image segment masks and expand their future training data sets.

## MVP

