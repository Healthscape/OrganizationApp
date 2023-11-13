# Healthscape Organization Application

The Healthscape application is an application intended for entities that have organizational or institutional roles. Each organization interacts in a decentralized data system that is the blockchain. Network users generate transactions, share and store data.

## Table of Contents

- [The Solution Architecture](#the-solution-architecture)
- [Features](#features)
- [Running](#running)

## The Solution Architecture

## Features

## Running
Docker makes it easy to set up that closely mirrors the production environment without having to install and configure all the dependencies on your local machine. Especially useful for working on complex applications that rely on many different libraries and tools.

The main purpose of the application is to enable communication with the blockchain network. Therefore, it is necessary to start the network beforehand. In order for the application to be recognized by the network, certificates are required. They can be found inside the [Healthscape Network](todooo) project in the path [/healthscape-network/organizations/organizations/peerOrganizations](todooo), and they need to be copied inside the resource directory of this project.

The respective [./docker-compose.yml](docker-compose.yml) will provide all the necessary resources, with public exposure to the connection ports:
```
docker-compose up -d
```
