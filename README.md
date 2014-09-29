# BAVRD-core
This is the core module of the BAVRD project, pronounced like 'Bav-hard'.

This project is an attempt at a modular chatbot, inspired by GitHub's Hubot, built upon the Vert.x platform thus allowing polyglot modules.

##BAVRD is heavily work-in-progress!

##Usage
The module starting point is MainBot. You can execute it on the Vert.x platform and pass it a json configuration file.
This file will allow to configure the bot by listing plugins configurations under the "modules" attribute.

Each plugin entry, or bavrd module, must describe the name of the module, a Verticle reference to load it and the module's configuration (which depends on the module).

BAVRD moduleRef entries are used to load the module's Vert.x Verticle (the code of the module).

A BAVRD configuration should also include a "botName" entry and modules should include one Face module and at most one Brain module (respectively to receive/send chat commands and to store/retrieve persistent data).

For now, running the bot is a bit rough around the edges : you will need maven and [vert.x](http://vertx.io/install.html) installed:
 - build the project using maven : `mvn compile` (or build it at least once using your IDE of choice, eg CTRL-F9 in IntelliJ)
 - prepare a configuration file named bavrd.json at the root of the project (see below or work from `bavrd-DEFAULT.json`)
 - run the bot using the pre-configured vertx maven task : `mvn vertx:runMod`
 - you should see a log entry for the initialization of each module that you declared in the configuration, provided corresponding module files are either in the src/java/ or resources/ paths.

##Example of a BAVRD configuration (bavrd.json):

    {
        "botName": "bavrd",
        "modules": [
            {
                "moduleName": "face",
                "moduleRef": "net.bavrd.faces.SlackFace",
                "moduleConf": {
                    "port": 8080,
                    "route": "/slack/incoming",
                    "api_token": "1234"
                }
            },
            {
                "moduleName": "echo",
                "moduleRef": "net.bavrd.limbs.Echo",
                "moduleConf": {
                    "sayFormat": "%u says '%m'"
                }
            }
        ]
    }

 - *`botName`* : when sending messages, under what name the BAVRD bot should act
 - *`modules.moduleName`* : for now, module names are only used internally and for logging
 - *`modules.moduleRef`* : see [vert.x verticles adressing reference](http://vertx.io/manual.html#running-vertx), for a java class it's a [FQN](# "Fully Qualified Name") if in the `src/java` dir, or *xxx.java* source file if in `resources/` dir
 - **SlackFace module**:
 in the module configuration of the face below, the Slack face allows to listen on a specific port + route, and requires an API token, which are part of the module's configuration

    {
       "moduleName": "face",
       "moduleRef": "net.bavrd.faces.SlackFace",
       "moduleConf": {
           "port": 8080,
           "route": "/slack/incoming",
           "api_token": "1234"
       }
    }

 - **Echo module**: echo is a simple demo BAVRD module that reacts to commands, a LIMB of the bot. Echo repeats what is send to it through a "say XXX" command.

 `"sayFormat": "%u says '%m'"` : The configuration allows a "sayFormat" describing what echo will send back : `%u` is replaced by sender's name, `%m` by the original message after the command.

##BAVRD modules basics (creating a new Limb)
What you probably want to do with your new shiny robot is to put together a custom script to make it react to command YOU have tailored...

Wait no more, this is easy :) You can develop scripts for BAVRD in any language Vert.x supports, including Java, Ruby, Python and JavaScript !

The basic principle is that the bot makes heavy use of the Vert.x eventBus : it will publish standardized messages on the `bavrd-incoming` address and expect modules to inspect the messages, find if there is a recognized command in it and if so, reply accordingly on the `bavrd-outgoing` event bus address. Said reply will in turn be processed in an ad hoc manner by the active Face and sent to the chatroom.
