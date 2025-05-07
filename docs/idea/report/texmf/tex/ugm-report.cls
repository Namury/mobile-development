\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{ugm-report}

\newif{\ifusetimesfonts}
\DeclareOption{times}{\usetimesfontstrue}

\DeclareOption*{\PassOptionsToClass{\CurrentOption}{report}}
\ExecuteOptions{ugm-report}
\ProcessOptions{}\relax{}
\LoadClass[12pt]{report}

\RequirePackage{geometry}
\RequirePackage{setspace}
\RequirePackage{pdflscape}
\RequirePackage{fontspec}
\RequirePackage{indentfirst}
\RequirePackage{hyperref}
\RequirePackage{titlesec}
\RequirePackage{graphicx}
\RequirePackage{textcomp}

\setmainfont{TimesNewRoman-Regular.ttf}[
	BoldFont = TimesNewRoman-Bold.ttf,
	ItalicFont = TimesNewRoman-Italic.ttf,
	BoldItalicFont = TimesNewRoman-BoldItalic.ttf
]

%---------------------------------------------------------------------------------------------------
% Layout
%---------------------------------------------------------------------------------------------------

\geometry{
	% showframe = true,
	a4paper,
	papersize = {215mm, 280mm},
	top = 3cm,
	left = 3cm,
	bottom = 3cm,
	right = 2.5cm,
	marginpar = 0pt,
	marginparwidth = 0pt,
	headsep = 2cm
}

%---------------------------------------------------------------------------------------------------
% Spacing
%---------------------------------------------------------------------------------------------------

\onehalfspacing{}
\normalsize{}

%---------------------------------------------------------------------------------------------------
% Section Formatting
%---------------------------------------------------------------------------------------------------
\def\thesection{\arabic{section}}
\titleformat*{\section}{\large\bfseries}

\endinput{}