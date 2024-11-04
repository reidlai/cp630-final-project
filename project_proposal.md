---
title:  'CP630 Project Proposal<br>LightGBM for Credit Card Fraud Detection'
author: Wai Kei LAI
date: 2024-10-16
---
# Introduction

In the period of advancement of technology and financial development, there has been a drastic increase in such activities as credit card payments, banking, online fund transactions and others as a result of the expansion of information technology. While this increase in financial activities has made capital and resource deployment to be more effective, it has also exactly led to the increase of fraudulent activities. Because of the arising complexity of the fraudulent transaction, its defenders fall behind the old solutions and seek for advanced means for counterfeiting and fraud deterrence.

Option Consommateurs (2024) published a report which showed that around 30% of the Canadians remember themselves being a victim of the bank fraud. In fact, among those who felt the effects of bank fraud, almost 25% have taken some measure to defend themselves after the incident. This indicates that the aftermath of such kinds of fraud is not only loss of money but also loss of peace of mind, and victims become more vigilant on the security of their assets.

# Problem Solving and Algorithms

Over the past few years, ‘machine learning’ techniques have developed as one of the recent technologies for solving problems related to fraud detection in financial transactions. Before applying any model, it is understood that using machine learning requires a large volume of data which comprises past activities connected with fraud and instigates models to recognize them efficiently. Such models can also be designed using historical data on transactions and will keep getting better as additional data will continue becoming available.

In response to this pressing issue, various machine learning techniques have been proposed for fraud detection. One such technique involves the use of LightGBM for Credit Card Fraud Detection (Huang, 2020). This innovative approach to fraud detection could potentially be applied to our project, offering a promising solution to this pervasive problem with supporting  research by Du et al. (2023).

# Proposed System Design

The system is subdivided into several vital containers as follows:

| Solution Component | Description |
| --- | --- |
| Machine Learning Model | This is the pre-built open source machine learning model download from external source. This model was built based on LightGBM. |
| Payment Processing Service | This is public facing service that response for the processing of payments from merchants who made the Payment request. |
| Payment Request Store | This relational database store gets to store payment requests and payment request status. |
| Machine Learning Inference Service | This services mainly serve all fraud detection request against incoming payment request. It will get and use machine learning model to identify any possible frauds. |
| Payment Notification Service | This is outside system which sends email alerts to the Customer especially when a Transaction made is on hold for suspected fraudulent activity. |

# Proposed Technical Stack

The given technical stack utilizes commonly used or well known technologies and frameworks thus making it secure and easily adaptable to changes. The payment solution with fraud detection based on machine learning relies on the combination of Spring Boot as backend, Svelte as frontend, Mage.AI for MLOps, PostgreSQL for data storage, Docker for virtual technology and SVM as algorithm for dynamic fraud detection in payments.

| Technology Component | Technology | Description |
| --- | --- | --- |
| Frontend UI + API | Spring Boot and Svelte | **Spring Boot** is utilized to create REST APIs of various services such as Payment Processing Service, Model Inference Service and more. **Svelte** will be utilized on Dataset Request UI as well as on Payment Order UI. |
| Database | PostgreSQL | **PostgreSQL** is a complete, open source and high performance database management system. PostgreSQL can be used to develop the Payment Request Store. |
| Containerization | Docker and Docker Compose | **Docker** is a tool that can be used to deploy backend services, frontend applications and other components by isolating all the parts in a uniform environment. **Docker Compose** is useful in the management of multiple containers that have been defined with the help of Docker and running them so as to orchestrate the numerous parts of the architecture. |
| Machine Learning Model | ONNX | **ONNX** Is a standard interface to represent ML models in its most generic form and can be used for the deployment of the trained SVM model for inference in the Model Inference Service. |

# Project Timeline

The timeline takes into consideration the major steps mentioned in the project proposal such as dataset acquisition, MLOps workflow, AI model, Transaction and Customer Notification. It similarly takes into consideration the integration, testing and the deployment phases.

1. **Payment Processing and Fraud Detection (2 weeks: Nov 1 –  Nov 14)**
2. **Notification Development and Email server  (1 weeks: Nov 15 - Nov 21)**
3. **Machine Learning Inference Service (2 weeks: Nov 22 – Dec 5)**
4. **Integration and testing (1 week: Dec 6 – Dec 12)**
5. **Documentation and deployment (1 week: Dec 13 – Dec 16)**

# Reference

1. Du, H. et al. (2023). AutoEncoder and LightGBM for Credit Card Fraud
Detection Problems. *Symmetry* 2023, 15, 870. [https://doi.org/10.3390/sym15040870](https://doi.org/10.3390/sym15040870)
2. Huang, K. (2020). An Optimized LightGBM Model for Fraud Detection. Journal of Physics: Conference Series, 1651, 012111, IOP Publishing. [https://doi.org/10.1088/1742-6596/1651/1/012111](https://doi.org/10.1088/1742-6596/1651/1/012111)
3. Option Consommateurs. (April 17, 2024). 30% of Canadians victims of bank fraud. Accessed October 14, 2024. [https://option-consommateurs.org/en/communique-barometre-2024/](https://option-consommateurs.org/en/communique-barometre-2024/)