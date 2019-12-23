# Notes

- Concurrency Vs Parallelism
- Concurrency means running task overlapping
- Parallelism really parallel run

- non blocking vs blocking

- sync vs async

## race condition =>
	Two threads change the same mutable state at the same time.
	Operations to run in same order (p1 then p2)

	Actor A => C
	Actor B => C

	C can receive messages from A, B in any ordder


## Actors
- Actor is an object encapsulating state and behaviour
- Actor is like person
- Actors talks to each other with messages
- Actors can form hierarchy

## Hierarchy
- eg: In software team we have PM, multiple TLs, under each TL we have designers,
QA, developers
- Each leader can do work himself, or distribute it to his team, and check status of work

- Actor starts child actors, if  child need something it asks the parent (Supervisor) for help

## Actor System
- Root of all actors structure
- Actor system is unit for managing shared facitlites like Scheduling, Config, Logging etc.,

### Actor system Components

1. Dead Letter Office - has all idle actors (not running)
2. User Guardian Actor - parent of all the actors which are created
3. System Guardian Actor - These are actors created by internal system
4. Scheduler - for normal scheduler operations
5. Event System - Log messages, publish messages etc.,
6. Configuration - Handles configuration

## Actor Components
- If we open Actor black box we have
  1. Actor Instance
  2. Mail Box
  3. Dispatcher (engine)
  4. ActorRef (Interface)

```
 Outside world ->(sends) ActorRef ->(dispatch) -> Message Dispatcher -> (Runs) -> MailBox ->(invoke) -> Actor
                                                                     -> (pubilsh) -> Message Queue -> MailBox
```

## Actor Lifecycle
- Initialized (actorOf)
preStart
Started
(if stop is received) Stopped
postStop
Terminated

- if(exception) Affected
- Resume
  - preRestart
  - postStop
  - postRestart
  - preStart
  - Started
- Stop

## Props
- Configuration for creation of actors

## Create Actor
Extend Actor trait and ovverride Receive


## Messages
- Tell (!) (Fire and Forget)
- Ask (?) Returns a future with reply

## Supervisor
- Parent creates child, so parent reacts when child fails
- Parent is kind of supervisor for the child
Parent has 4 choices
- Resume (keeps internal state)
- Restart (cleans internal state)
- Escalate (Parent dies and sends it to its parent)
- Stop (stops the child permanently)



