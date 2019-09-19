TGT := client

default: $(TGT)

$(TGT): $(TGT).cpp
	$(CXX) -Wall -o $@ $<

clean:
	rm -f $(TGT)

.PHONY: clean
