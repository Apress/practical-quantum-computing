# https://github.com/QISKit/qiskit-tutorial/blob/002d054c72fc59fc5009bb9fa0ee393e15a69d07/1_introduction/getting_started.ipynb

import sys
sys.path.append('../../qiskit-sdk-py/') # solve the relative dependencies if you clone QISKit from the Git repo and use like a global.

from qiskit import QuantumProgram
import Qconfig

# Creating Programs
# create your first QuantumProgram object instance.
#qp = QuantumProgram()

# Creating Registers
# create your first Quantum Register called "qr" with 2 qubits 
#qr = qp.create_quantum_register('qr', 2)
# create your first Classical Register  called "cr" with 2 bits
#cr = qp.create_classical_register('cr', 2)

# Creating Circuits
# create your first Quantum Circuit called "qc" involving your Quantum Register "qr"
# and your Classical Register "cr"
#qc = qp.create_circuit('Circuit', [qr], [cr])

Q_SPECS = {
    'circuits': [{
        'name': 'Circuit',
        'quantum_registers': [{
            'name': 'qr',
            'size': 4
        }],
        'classical_registers': [{
            'name': 'cr',
            'size': 4
        }]}],
}

qp = QuantumProgram(specs=Q_SPECS)

##########################################
# Get the components.

# get the circuit by Name
circuit = qp.get_circuit('Circuit')

# get the Quantum Register by Name
quantum_r = qp.get_quantum_register('qr')

################################################

# get the Classical Register by Name
classical_r = qp.get_classical_register('cr')

# Pauli X gate to qubit 1 in the Quantum Register "qr" 
circuit.x(quantum_r[1])

# Pauli Y gate to qubit 2 in the Quantum Register "qr" 
circuit.y(quantum_r[2])

# Pauli Z gate to qubit 3 in the Quantum Register "qr" 
circuit.z(quantum_r[3])

# CNOT (Controlled-NOT) gate from qubit 0 to qubit 2
circuit.cx(quantum_r[0], quantum_r[2])

# add a barrier to your circuit
circuit.barrier()

# H (Hadamard) gate to qubit 0 in the Quantum Register "qr" 
circuit.h(quantum_r[0])

# S Phase gate to qubit 0
circuit.s(quantum_r[0])

# T Phase gate to qubit 1
circuit.t(quantum_r[1])

# identity gate to qubit 1
circuit.iden(quantum_r[1])

# first physical gate: u1(lambda) to qubit 0
circuit.u1(0.3, quantum_r[0])

# second physical gate: u2(phi,lambda) to qubit 1
circuit.u2(0.3, 0.2, quantum_r[1])

# second physical gate: u3(theta,phi,lambda) to qubit 2
circuit.u3(0.3, 0.2, 0.1, quantum_r[2])

# rotation around the x-axis to qubit 0
circuit.rx(0.2, quantum_r[0])

# rotation around the y-axis to qubit 1
circuit.ry(0.2, quantum_r[1])

# rotation around the z-axis to qubit 2
circuit.rz(0.2, quantum_r[2])

# Classical if, from qubit2 gate Z to classical bit 1
# circuit.z(quantum_r[2]).c_if(classical_r, 0)

# measure gate from qubit 0 to classical bit 0
circuit.measure(quantum_r[0], classical_r[0])
circuit.measure(quantum_r[1], classical_r[1])
circuit.measure(quantum_r[2], classical_r[2])

############ Circuit names
qp.get_circuit_names()


############# QASM
# QASM from a program

QASM_source = qp.get_qasm('Circuit')

#print(QASM_source)

################ Compile & run in simulator
# Sample stdout
#(qiskit) [centos@localhost qiskit]$ python basic-test.py 
#{'0101': 238, '0110': 225, '0100': 365, '0111': 164, '0010': 8, '0000': 7, '0001': 11, '0011': 6}

backend = 'local_qasm_simulator' 
circuits = ['Circuit']  # Group of circuits to execute

qobj = qp.compile(circuits, backend) # Compile your program

result = qp.run(qobj, wait=2, timeout=240)

##print(result)
print ("Content-type: application/json\n\n")
# result.get_counts - dict object
print (str(result.get_counts('Circuit')).replace("'", "\""))

############### Run on real device
## Backend where you execute your program; in this case, on the Real Quantum Chip online
#backend = 'ibmqx4'    
#circuits = ['Circuit']   # Group of circuits to execute
#shots = 1024           # Number of shots to run the program (experiment); maximum is 8192 shots.
#max_credits = 3          # Maximum number of credits to spend on executions. 

#qp.set_api(Qconfig.APItoken, Qconfig.config['url']) # set the APIToken and API url

## ERROR WITH PARAM , silent=False -
## Traceback (most recent call last):
##  File "basic-test.py", line 141, in <module>
##    result_real = qp.execute(circuits, backend, shots=shots, max_credits=3, wait=10, timeout=240, silent=False)
## TypeError: execute() got an unexpected keyword argument 'silent'

# , silent=False -
#result_real = qp.execute(circuits, backend, shots=shots, max_credits=3, wait=10, timeout=240)

#print(result_real.get_counts('Circuit'))


