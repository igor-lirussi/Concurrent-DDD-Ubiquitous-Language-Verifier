------------------------- MODULE model_check -------------------------

EXTENDS Integers, Sequences, TLC, FiniteSets

FILES==2
WORDSINFILES==3
TOTWORDS==FILES*WORDSINFILES

(*--algorithm model_check_in_pluscal 

variables
  continue = TRUE,
  counter = 0;

define
    ProperFinalValue == <>(counter = TOTWORDS)
    NoOverflowInvariant == (counter < TOTWORDS+1) 
end define;

fair process ctrl = "ctrl" (* controller that changes flag when asked from GUI*)
begin
  Cycle: 
  while TRUE do
       continue := FALSE;
l2:    continue := TRUE;
  end while;
end process;

fair process agent \in 1..FILES  (*agent that goes in file*)
variables words = WORDSINFILES;
begin
l1: while words > 0 do
l3:   if continue then  (*check flag continue*)
l4:     words := words - 1; (*read word, or chunk, from file*)
l5:     counter := counter + 1;     (*update model*)
        print counter;    (*call method gui to print *)
      end if;   
    end while;
end process;

end algorithm;*) 

\* BEGIN TRANSLATION (chksum(pcal) = "2f0d3d52" /\ chksum(tla) = "71360d0c")
VARIABLES continue, counter, pc

(* define statement *)
ProperFinalValue == <>(counter = TOTWORDS)
NoOverflowInvariant == (counter < TOTWORDS+1)

VARIABLE words

vars == << continue, counter, pc, words >>

ProcSet == {"ctrl"} \cup (1..FILES)

Init == (* Global variables *)
        /\ continue = TRUE
        /\ counter = 0
        (* Process agent *)
        /\ words = [self \in 1..FILES |-> WORDSINFILES]
        /\ pc = [self \in ProcSet |-> CASE self = "ctrl" -> "Cycle"
                                        [] self \in 1..FILES -> "l1"]

Cycle == /\ pc["ctrl"] = "Cycle"
         /\ continue' = FALSE
         /\ pc' = [pc EXCEPT !["ctrl"] = "l2"]
         /\ UNCHANGED << counter, words >>

l2 == /\ pc["ctrl"] = "l2"
      /\ continue' = TRUE
      /\ pc' = [pc EXCEPT !["ctrl"] = "Cycle"]
      /\ UNCHANGED << counter, words >>

ctrl == Cycle \/ l2

l1(self) == /\ pc[self] = "l1"
            /\ IF words[self] > 0
                  THEN /\ pc' = [pc EXCEPT ![self] = "l3"]
                  ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
            /\ UNCHANGED << continue, counter, words >>

l3(self) == /\ pc[self] = "l3"
            /\ IF continue
                  THEN /\ pc' = [pc EXCEPT ![self] = "l4"]
                  ELSE /\ pc' = [pc EXCEPT ![self] = "l1"]
            /\ UNCHANGED << continue, counter, words >>

l4(self) == /\ pc[self] = "l4"
            /\ words' = [words EXCEPT ![self] = words[self] - 1]
            /\ pc' = [pc EXCEPT ![self] = "l5"]
            /\ UNCHANGED << continue, counter >>

l5(self) == /\ pc[self] = "l5"
            /\ counter' = counter + 1
            /\ PrintT(counter')
            /\ pc' = [pc EXCEPT ![self] = "l1"]
            /\ UNCHANGED << continue, words >>

agent(self) == l1(self) \/ l3(self) \/ l4(self) \/ l5(self)

Next == ctrl
           \/ (\E self \in 1..FILES: agent(self))

Spec == /\ Init /\ [][Next]_vars
        /\ WF_vars(ctrl)
        /\ \A self \in 1..FILES : WF_vars(agent(self))

\* END TRANSLATION 


=============================================================================
 
