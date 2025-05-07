# vim: set ft=perl ts=2 sw=2 sts=2 et encoding=utf-8:

# Main configuration settings
$pdf_mode = 1;
$pdflatex = 'lualatex -synctex=1 -shell-escape -interaction=nonstopmode -file-line-error -halt-on-error';
$out_dir = 'build';

# Set local texmf tree
ensure_path('TEXMFHOME', './texmf//');
# Set local texmf.cnf
ensure_path('TEXMFCNF', './');

# Required modules
use File::Spec;
use File::Path qw(make_path);

# Create build directories before compilation
sub create_build_directories {
  # Get all .tex files and create corresponding build directories
  my @tex_files = glob("*.tex */*.tex");
  my %dirs_to_create;

  foreach my $tex_file (@tex_files) {
    my ($volume, $directories, $file) = File::Spec->splitpath($tex_file);
    if ($directories) {
      $dirs_to_create{File::Spec->catdir($out_dir, $directories)} = 1;
    }
  }

  # Create directories if needed
  make_path(keys %dirs_to_create) if %dirs_to_create;
}

create_build_directories();
