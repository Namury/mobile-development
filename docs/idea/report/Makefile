all: main.pdf

main.pdf:
	latexmk main.tex

clean:
	latexmk -C

format:
	find . \( -name "*.tex" -o -name "*.cls" -o -name "*.sty" \) -type f | xargs latexindent -wd -s -l -c=build/
