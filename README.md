## Welcome to Light

This is my current devalopment project for April 2023 onwards. Light is a lighting control software that (currently) leans heavily on the design and influence of MA and Hog softwares. I want to build a lighting software that can run efficiently on a laptop, and brings together all the good elements of lighting consoles that I use every day while putting in features that I would find useful/removing the problems I have with industry standard consoles in the hope that this is useful to other lighting techs.

## Folder Structure

So far front end and back end are seperated with a networking sub folder to be introduced when I get to that stage

- `light.gui.*`: contains all gui base components and infrastructure and backend interface implementing classes
- `light.*`: all backend control components

The idea is extremely loose coupling so one gui 'look' can be swapped out for future updates and stuff

# Components I intend to add over the course of the project include:

- Outputting directly from the laptop via ArtNet, sACN as well as outputting MANet and HogNet (without the distributed processing).
- 3D visualiser using work from one of my pervious repos.
- Support for MIDI controllers - plug and play style. Idea is to be able to map the button or fader of any detected controller to basically any element of the control software to allow maximum control and usability for operators.
