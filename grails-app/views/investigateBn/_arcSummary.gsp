<fieldset class="default">
	<legend>${parent} &rarr; ${child}</legend>

	<bnInvestigate:arcSummary parent="${parent}" child="${child}" />

	<script type="text/javascript">
		$( '.user' ).hover( function() {
			var id = $( this ).find( 'input:hidden[name=id]' ).val();
			$( '.user-' + id).addClass( 'hover' );
		}, function() {
			var id = $( this ).find( 'input:hidden[name=id]' ).val();
			$( '.user-' + id).removeClass( 'hover' );
		})
	</script>

</fieldset>