# OAuth 2.0 Implementation

This repository contains a full implementation of the OAuth 2.0 protocol, including a Client, Authorization Server, and Resource Server. This project is designed to provide a foundational understanding of how OAuth 2.0 works and can be adapted or extended for specific use cases.

**Note:** This project still has some bugs, but they are being addressed :) .

# Introduction

OAuth 2.0 is an industry-standard protocol for authorization. It allows third-party applications to grant limited access to a user’s resources without exposing credentials. This project demonstrates a basic OAuth 2.0 flow with the following components:

## Authorization Server
Responsible for authenticating the user and granting access tokens.

## Resource Server
Hosts the protected resources and validates the access tokens.

## Client
Represents the application that requests access to the resources on behalf of the user.

# Local Project

This project is intended to be run locally and uses a local database for development and testing purposes.
