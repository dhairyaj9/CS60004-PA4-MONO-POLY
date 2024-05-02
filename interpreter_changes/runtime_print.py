import sys

#take in as ccmmand line argument name of file
# Take in the name of the txt file as a command line argument
file_name = sys.argv[1]

# Open the file
with open(file_name, 'r') as file:
    lines = file.readlines()


# Rest of the code...
static_runtime_invokes = 0
virtual_runtime_invokes = 0

# Iterate through each line in the file
for line in lines:
    # Split the line into two parts using comma as delimiter
    parts = line.strip().split(',')
    
    # Extract the values for static and virtual runtime invokes
    static = int(parts[0])
    virtual = int(parts[1])
    
    # Add to the sums
    static_runtime_invokes += static
    virtual_runtime_invokes += virtual

# Calculate the total runtime invokes
total_runtime_invokes = static_runtime_invokes + virtual_runtime_invokes

# Print the results
print("Static Runtime Invokes:", static_runtime_invokes)
print("Virtual Runtime Invokes:", virtual_runtime_invokes)
print("Total Runtime Invokes:", total_runtime_invokes)
