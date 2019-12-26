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
- Mostly for failed ones (take action)
- Parent creates child, so parent reacts when child fails
- Parent is kind of supervisor for the child
Parent has 4 choices
- Resume (keeps internal state)
- Restart (cleans internal state)
- Escalate (Parent dies and sends it to its parent)
- Stop (stops the child permanently)

Supervision Strategies
- One for One Strategy => Parent will apply only on child (recommend)
- One for all Strategy => Parent will apply on all children

## Monitoring
- For actors that are terminated (take action)

## Actor Ref
- Actor ref is interface of actor instance
- Reference for actor
- Created when actor is created, but still pointed to same actor when restart
- Points to DeadLetter when actor is shutdown
- When new actor instance is created ActorRef is created again

## Actor Path
/user/ActorA/ActorB - location of path in the system tree
recursive link following the supervisors

Diff is ActorPath is name based but ActorRef is reference based (so unique)

## Actor Selection
- Actor selection is created from name or path (not with reference)

## Routing in Akka
Routers can forward stuff to another actor
Use predefined routers mostly

## Become/Unbecome
Change actor behaviour in run time

## Stash Messages
- Stash trait enables actor to temporarily stash messages
- stash()
- unstashAll()
- Order is preserved

## FSM
A FSM is State(S) X Events(E) -> Action(A), State(S')

## Akka Persistence
Actor contains variable count, if actor crash then we lose internal state
We need to store the data of the actor, so when it restarts get it from external state

So in Akka we don't store the internal state, we just store the events so that the internal state can be recovered from the states

Akka -> Event => Save in DB

Gist:
Sender -> Command -> Actor (validates commands) -> Event (This event is stored in DB)

## Architecture of Akka Persistence
Persistent Actor
  implements commands and events
 it can persist events in a journal and can react to them in thread safe manner. It can be used to implement both command and event sourced actors.
 when such actor is started or restarted journaled messages are replayed to actor
Persistent View
  Stateful actor that receives journeled messages written by persistent actors, but it wont generate new messages
  It instead updates the internal state only from a persistent actor's replicated message stream.
  Deprecated in favour of PersistentQuery
Journal
  Async write journal, a journal that stores a sequence of events sent to a persistent actor.
  An application can control which messages a journal-ed and which are received by the persistent actor without being journaled.
Snapshot
   a snapshot store persists snapshots of a persistent actor. Snapshots are used for optimizing recovery times.
   The storage backend of a snapshot store is plugable. The default snapshot storage plugin writes to a local filesystem.

You can override recovery method to Recovery.None if you don't want recovery

## Persistence Query
In CQRS Pattern, persistence Query is nothing but implementing the Read side
Only queries the journal
