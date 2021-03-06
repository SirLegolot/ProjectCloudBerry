# Project CloudBerry
This is a copy from the original source repo that I made during my internship: https://github.com/googleinterns/step62-2020

![Cloudberry Logo](/project/src/main/webapp/images/logo_search.png)

Local businesses are a major factor that drives today’s national economy. In the United states, these businesses create more than two-thirds of the total new jobs and according to a report published by the Office of Advocacy for the U.S. small business administration, they account for 44% of the nation’s economic activity. However unlike large companies and corporations, these local businesses are more prone to the effects of events such as COVID where the customer base can decrease significantly. A solution that aims to bring customers to shop at these businesses can stimulate the local economies and reinvigorate the job market that has been affected.

We aim to create a web application to support local businesses, especially during these times with COVID-19. Business owners will be able to catalog their items on our application by simply taking a picture and having cloud vision automatically generate labels for the items. On the customer side, users can simply take a picture of the item they are looking for, and the web application will automatically find products from businesses nearby that are similar to it. This way, customers can go and support local businesses when buying items.

# How to run the web app
This app can be deployed to Google cloud appengine. First, clone the repo to your directory. Then navigate to the project folder inside. Simply call "mvn package appengine:run" to run in a development server or "mvn package appengine:deploy" to deploy to your website. On your cloud console you'll have to enable a couple of APIs such as cloud vision to get this to work.

# Contributers
 - Neel Gandhi (https://github.com/SirLegolot)
 - Phillips Olagunju (https://github.com/PhillipsOlagunju)
 - James Washington (https://github.com/mrjwashiv)

