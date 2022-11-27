package main

import (
	"fmt"
 	"io"
 	"os"
)

func check(e error) {
	if e != nil {
		panic(e);
	}
}

func main() {
	fmt.Println("Hello world");
	dat, err := os.ReadFile("lotr.en");
	check(err);

	f, err := os.Open("lotr.en");
	check(err);
}

