# Watson
Building a Productionalized Machine Learning Platform. This Insight project utilizes Sherlock for transfer learning and scales the it to serve many jobs at once while maintaining important information about the models and datasets used in order to assist Machine Learning Engineers determine the best course of action to take regarding their ML model and training datasets.

## Tech Stack

### Flask
UI

### PostgreSQL
Database containing information on models and datasets

### S3
Storage of raw image data

### Kubernetes
Deploy Sherlock Docker containers

### Kafka
Broker messages between different processes

## Data Source

OpenImages (https://storage.googleapis.com/openimages/web/download.html, 500GB)

## Engineering Challenge

Processing images on the Sherlock machine learning platform poses throughput and latency issues. Image datasets by nature are very dense and images take
long periods of time to process. Being unable to facilitate the distribution of machine learning jobs will lead to very high latency for this reason. This
is made even more challenging by the need to save image sets locally to perform ML jobs. It will be necessary to track jobs that have and have not been
completed and ensure that all messages are completed in a way that distributes the work in an efficient manner across the Sherlock nodes.

## Business Value

Computer vision is an increasingly popular subject in health science, manufacturing, and automotive among other industries. Many organizations have already begun implementing this technology into their products. But before their ML model was trained, it needed training data. Manually obtained training data. The process of segmenting, categorizing, and labelling data is so labor intensive that many companies have turned to outsourcing this task (Google, for example, uses their reCAPTCHA API to do this: https://www.google.com/recaptcha/intro/v3.html). However, by automating this task, engineers and researchers will be able to get more specific image segment masks and expand their future training data sets.

## MVP

Produce a demonstration of the inference results and how they can benefit a machine learning engineer trying to determine how to improve their next 
dataset.