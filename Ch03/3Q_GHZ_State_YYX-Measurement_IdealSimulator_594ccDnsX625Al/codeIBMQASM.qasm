//OPENQASM 2.0
IBMQASM 2.0;

include "qelib1.inc";

qreg q[3];
creg c[3];

h q[0];
h q[1];
x q[2];
cx q[1],q[2];
cx q[0],q[2];
h q[0];
h q[1];
h q[2];
barrier q[0],q[1],q[2];
sdg q[0];
sdg q[1];
h q[0];
h q[1];
h q[2];
measure q[0] -> c[0];
measure q[1] -> c[1];
measure q[2] -> c[2];
