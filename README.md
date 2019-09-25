# Watson
Building a Productionalized Machine Learning Platform. This Insight project utilizes Sherlock for transfer learning and scales the this machine learning project to serve many jobs at once while maintaining important information about the models and datasets used.

## Tech Stack

### Flask
UI

### Cassandra
Database containing information on models and datasets

### S3
Storage of raw image data

### Kubernetes
Deploy Sherlock Docker containers

### Kafka
Broker messages between different processes

### Spark
Sink model and dataset information into Cassandra

## Data Source

OpenImages (https://storage.googleapis.com/openimages/web/download.html, 500GB)

## Engineering Challenge

Large-scale image classification itself is tasking due to the need to preprocess images before being used for training and validating. The scale becomes even larger when segmenting images into masks. Such processing on a single machine for an entire dataset takes a lot of time and there is too much room for failure within these periods. But by scaling out computations, I will have to determine ways to make the process fault-tolerant.

## Business Value

Computer vision is an increasingly popular subject in health science, manufacturing, and automotive among other industries. Many organizations have already begun implementing this technology into their products. But before their ML model was trained, it needed training data. Manually obtained training data. The process of segmenting, categorizing, and labelling data is so labor intensive that many companies have turned to outsourcing this task (Google, for example, uses their reCAPTCHA API to do this: https://www.google.com/recaptcha/intro/v3.html). However, by automating this task, engineers and researchers will be able to get more specific image segment masks and expand their future training data sets.

## MVP

Compile an image dataset of categorized segment masks from a large dataset of unsegmented images using a pipeline which runs a pretrained ML model.
